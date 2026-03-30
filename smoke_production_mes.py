import json
import urllib.request
import urllib.error

BASE = 'http://localhost:8080'


def post_json(url, payload):
    data = json.dumps(payload).encode('utf-8')
    req = urllib.request.Request(url, data=data, headers={'Content-Type': 'application/json'}, method='POST')
    with urllib.request.urlopen(req, timeout=10) as resp:
        return json.loads(resp.read().decode('utf-8'))


def get_json(url, token):
    req = urllib.request.Request(url, headers={'Authorization': f'Bearer {token}'}, method='GET')
    with urllib.request.urlopen(req, timeout=10) as resp:
        return json.loads(resp.read().decode('utf-8'))


def main():
    login = post_json(f'{BASE}/api/v1/auth/login', {'username': 'admin', 'password': 'admin123'})
    if login.get('code') != 200:
        raise SystemExit(f"login failed: {login}")
    token = login['data']['accessToken']

    checks = [
        ('菜单路由', f'{BASE}/api/v1/system/menus/routers'),
        ('生产看板统计', f'{BASE}/api/v1/api/production/dashboard/statistics'),
        ('生产订单分页', f'{BASE}/api/v1/production/order/page'),
        ('工序列表', f'{BASE}/api/v1/api/process/list'),
        ('扎包列表', f'{BASE}/api/v1/api/bundle/list'),
        ('AQL标准分页', f'{BASE}/api/v1/api/quality/aql'),
        ('巡检记录分页', f'{BASE}/api/v1/api/quality/patrols'),
        ('返工单分页', f'{BASE}/api/v1/api/quality/reworks'),
        ('裁床单分页', f'{BASE}/api/v1/production/cut-order/page'),
        ('裁床扎包分页', f'{BASE}/api/v1/production/cut-bundle/page'),
    ]

    ok_all = True
    for name, url in checks:
        try:
            res = get_json(url, token)
        except urllib.error.HTTPError as e:
            ok_all = False
            print(f'[FAIL] {name}: http={e.code}')
            continue
        except Exception as e:
            ok_all = False
            print(f'[FAIL] {name}: {e}')
            continue

        code = res.get('code')
        if code != 200:
            ok_all = False
            print(f"[FAIL] {name}: code={code}, msg={res.get('msg')}")
            continue
        print(f'[OK]   {name}: code=200')

    routers = get_json(f'{BASE}/api/v1/system/menus/routers', token)
    routers_text = json.dumps(routers, ensure_ascii=False)
    print('[OK]   菜单包含 /production:', '/production' in routers_text)
    print('[OK]   菜单包含 /mes:', '/mes' in routers_text)

    raise SystemExit(0 if ok_all else 1)


if __name__ == '__main__':
    main()
