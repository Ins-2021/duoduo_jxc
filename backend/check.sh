#!/usr/bin/env bash
set -euo pipefail

PORT="${1:-8080}"
CONTEXT_PATH="${2:-/api}"
ACTION="${3:-ask}"
HEALTH_URL="http://localhost:${PORT}${CONTEXT_PATH}/actuator/health"

HEALTH_RESPONSE="$(curl -s "${HEALTH_URL}" || true)"
LISTEN_INFO="$(lsof -nP -iTCP:${PORT} -sTCP:LISTEN 2>/dev/null || true)"
PIDS="$(lsof -t -nP -iTCP:${PORT} -sTCP:LISTEN 2>/dev/null | sort -u || true)"

echo "检查地址: ${HEALTH_URL}"
if [[ "${HEALTH_RESPONSE}" == *"\"status\":\"UP\""* ]]; then
  echo "健康检查: UP"
else
  echo "健康检查: DOWN"
fi

if [[ -n "${LISTEN_INFO}" ]]; then
  echo "端口监听:"
  echo "${LISTEN_INFO}"
else
  echo "端口监听: 未发现 ${PORT} 端口监听进程"
fi

if [[ -n "${PIDS}" ]]; then
  SHOULD_KILL="n"
  case "${ACTION}" in
    kill|yes|y)
      SHOULD_KILL="y"
      ;;
    skip|no|n)
      SHOULD_KILL="n"
      ;;
    ask)
      if [[ -t 0 ]]; then
        read -r -p "检测到 Java 进程监听端口 ${PORT}（PID: ${PIDS//$'\n'/,}），是否终止？(y/N): " USER_CHOICE
        USER_CHOICE="${USER_CHOICE:-N}"
        if [[ "${USER_CHOICE}" =~ ^[Yy]$ ]]; then
          SHOULD_KILL="y"
        fi
      else
        echo "非交互环境，默认不终止 Java 进程。可传第三个参数 kill 强制终止。"
      fi
      ;;
    *)
      echo "未知操作参数: ${ACTION}，可选值: ask|kill|skip"
      ;;
  esac

  if [[ "${SHOULD_KILL}" == "y" ]]; then
    while IFS= read -r PID; do
      if [[ -n "${PID}" ]]; then
        kill "${PID}" || true
      fi
    done <<< "${PIDS}"
    sleep 1
    REMAIN_PIDS="$(lsof -t -nP -iTCP:${PORT} -sTCP:LISTEN 2>/dev/null | sort -u || true)"
    if [[ -n "${REMAIN_PIDS}" ]]; then
      while IFS= read -r PID; do
        if [[ -n "${PID}" ]]; then
          kill -9 "${PID}" || true
        fi
      done <<< "${REMAIN_PIDS}"
      sleep 1
      REMAIN_PIDS="$(lsof -t -nP -iTCP:${PORT} -sTCP:LISTEN 2>/dev/null | sort -u || true)"
    fi
    if [[ -n "${REMAIN_PIDS}" ]]; then
      echo "进程终止结果: 部分进程仍在运行（PID: ${REMAIN_PIDS//$'\n'/,}）"
    else
      echo "进程终止结果: 已终止监听 ${PORT} 的 Java 进程"
      LISTEN_INFO=""
    fi
  else
    echo "进程处理: 保留现有 Java 进程"
  fi
fi

if [[ "${HEALTH_RESPONSE}" == *"\"status\":\"UP\""* && -n "${LISTEN_INFO}" ]]; then
  echo "结论: 后端已启动并可用"
  exit 0
fi

echo "结论: 后端未启动或不可用"
exit 1
