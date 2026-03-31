#!/usr/bin/env python3
"""
报表统计模块测试脚本
测试多多进销存系统的报表统计功能
"""
import urllib.request
import urllib.error
import json
from datetime import datetime

BASE_URL = "http://localhost:8080"

class ReportTester:
    def __init__(self):
        self.token = None
        self.test_results = []
        self.test_data = []

    def login(self):
        """登录获取token"""
        print("=" * 60)
        print("1. 用户登录")
        print("=" * 60)
        try:
            req = urllib.request.Request(
                f"{BASE_URL}/api/v1/auth/login",
                data=json.dumps({"username": "admin", "password": "admin123"}).encode(),
                headers={"Content-Type": "application/json"},
                method="POST"
            )
            with urllib.request.urlopen(req) as response:
                data = json.loads(response.read().decode())
                if data.get("code") == 200:
                    self.token = data["data"]["accessToken"]
                    print(f"✓ 登录成功，Token获取成功")
                    return True
                else:
                    print(f"✗ 登录失败: {data}")
                    return False
        except Exception as e:
            print(f"✗ 登录异常: {e}")
            return False

    def make_request(self, method, path, data=None, params=None):
        """发送API请求"""
        url = f"{BASE_URL}{path}"
        if params:
            query = "&".join([f"{k}={v}" for k, v in params.items()])
            url = f"{url}?{query}"

        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }

        try:
            if method == "GET":
                req = urllib.request.Request(url, headers=headers, method="GET")
            elif method == "POST":
                req = urllib.request.Request(
                    url,
                    data=json.dumps(data).encode() if data else None,
                    headers=headers,
                    method="POST"
                )
            else:
                req = urllib.request.Request(
                    url,
                    data=json.dumps(data).encode() if data else None,
                    headers=headers,
                    method=method
                )

            with urllib.request.urlopen(req, timeout=30) as response:
                result = json.loads(response.read().decode())
                return {"success": True, "data": result}
        except urllib.error.HTTPError as e:
            try:
                error_data = json.loads(e.read().decode())
                return {"success": False, "error": f"HTTP {e.code}: {error_data}", "code": e.code}
            except:
                return {"success": False, "error": f"HTTP {e.code}: {e.reason}", "code": e.code}
        except Exception as e:
            return {"success": False, "error": str(e)}

    def safe_get(self, d, *keys, default=None):
        """安全获取嵌套字典的值"""
        result = d
        for key in keys:
            if isinstance(result, dict):
                result = result.get(key, default)
            else:
                return default
        return result if result is not None else default

    def test_profit_analysis(self):
        """测试利润分析模块"""
        print("\n" + "=" * 60)
        print("2. 利润分析模块测试")
        print("=" * 60)

        module_results = {"module": "利润分析", "tests": []}
        startDate = "2026-01-01"
        endDate = "2026-03-30"
        params = {"startDate": startDate, "endDate": endDate, "pageNum": 1, "pageSize": 10}

        # 2.1 利润汇总查询
        print("\n--- 2.1 利润汇总查询 ---")
        result = self.make_request("GET", "/api/v1/api/report/profit/summary", params=params)
        test_case = {
            "name": "利润汇总查询",
            "endpoint": "GET /api/v1/api/report/profit/summary",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 利润汇总查询成功")
            profit_data = self.safe_get(result, "data", "data", default={})
            print(f"  - 总销售额: {profit_data.get('totalSalesAmount', 0)}")
            print(f"  - 总成本: {profit_data.get('totalCostAmount', 0)}")
            print(f"  - 总利润: {profit_data.get('totalGrossProfit', 0)}")
            print(f"  - 平均利润率: {profit_data.get('avgGrossProfitRate', 0)}%")
            print(f"  - 订单总数: {profit_data.get('totalOrderCount', 0)}")
            test_case["data"] = profit_data
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 利润汇总查询失败: {error_msg}")
        module_results["tests"].append(test_case)

        # 2.2 款式利润排名
        print("\n--- 2.2 款式利润排名 ---")
        result = self.make_request("GET", "/api/v1/api/report/profit/style-ranking", params=params)
        test_case = {
            "name": "款式利润排名",
            "endpoint": "GET /api/v1/api/report/profit/style-ranking",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        ranking_data = []
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 款式利润排名查询成功")
            ranking_data = self.safe_get(result, "data", "data", default=[])
            print(f"  - 返回款式数量: {len(ranking_data)}")
            if ranking_data:
                print(f"  - TOP1款式: {ranking_data[0].get('styleName', 'N/A')} - 利润: {ranking_data[0].get('grossProfit', 0)}")
            test_case["data"] = ranking_data[:5]
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 款式利润排名查询失败: {error_msg}")
        module_results["tests"].append(test_case)

        # 2.3 客户利润排名
        print("\n--- 2.3 客户利润排名 ---")
        result = self.make_request("GET", "/api/v1/api/report/profit/customer-ranking", params=params)
        test_case = {
            "name": "客户利润排名",
            "endpoint": "GET /api/v1/api/report/profit/customer-ranking",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        customer_data = []
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 客户利润排名查询成功")
            customer_data = self.safe_get(result, "data", "data", default=[])
            print(f"  - 返回客户数量: {len(customer_data)}")
            if customer_data:
                print(f"  - TOP1客户: {customer_data[0].get('customerName', 'N/A')} - 利润: {customer_data[0].get('grossProfit', 0)}")
            test_case["data"] = customer_data[:5]
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 客户利润排名查询失败: {error_msg}")
        module_results["tests"].append(test_case)

        # 2.4 利润趋势分析
        print("\n--- 2.4 利润趋势分析 ---")
        result = self.make_request("GET", "/api/v1/api/report/profit/trend", params=params)
        test_case = {
            "name": "利润趋势分析",
            "endpoint": "GET /api/v1/api/report/profit/trend",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 利润趋势分析查询成功")
            trend_data = self.safe_get(result, "data", "data", default={})
            monthly_data = trend_data.get('monthlyTrends', [])
            print(f"  - 月度趋势数据点: {len(monthly_data)}")
            if monthly_data:
                print(f"  - 最新月份: {monthly_data[-1].get('month', 'N/A')} - 利润: {monthly_data[-1].get('grossProfit', 0)}")
            test_case["data"] = trend_data
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 利润趋势分析查询失败: {error_msg}")
        module_results["tests"].append(test_case)

        # 2.5 款式利润明细
        print("\n--- 2.5 款式利润明细 ---")
        style_id = None
        if ranking_data and len(ranking_data) > 0:
            style_id = ranking_data[0].get('styleId')
        if style_id:
            result = self.make_request("GET", f"/api/v1/api/report/profit/style-detail/{style_id}", params=params)
            test_case = {
                "name": "款式利润明细",
                "endpoint": f"GET /api/v1/api/report/profit/style-detail/{style_id}",
                "params": params,
                "passed": False,
                "result": str(result)[:500]
            }
            if result["success"] and self.safe_get(result, "data", "code") == 200:
                test_case["passed"] = True
                print(f"✓ 款式利润明细查询成功")
                detail_data = self.safe_get(result, "data", "data", default={})
                print(f"  - 款式名称: {detail_data.get('styleName', 'N/A')}")
                print(f"  - 总销量: {detail_data.get('totalQuantity', 0)}")
                print(f"  - 总利润: {detail_data.get('totalGrossProfit', 0)}")
                test_case["data"] = detail_data
            else:
                error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
                print(f"✗ 款式利润明细查询失败: {error_msg}")
            module_results["tests"].append(test_case)
        else:
            print(f"⚠ 无法获取款式ID，跳过款式利润明细测试")
            module_results["tests"].append({
                "name": "款式利润明细",
                "endpoint": "GET /api/v1/api/report/profit/style-detail/{styleId}",
                "passed": False,
                "error": "无法获取款式ID进行测试"
            })

        self.test_results.append(module_results)
        return module_results

    def test_cost_report(self):
        """测试成本报表模块"""
        print("\n" + "=" * 60)
        print("3. 成本报表模块测试")
        print("=" * 60)

        module_results = {"module": "成本报表", "tests": []}
        params = {"pageNum": 1, "pageSize": 10}

        # 3.1 成本汇总查询
        print("\n--- 3.1 成本汇总查询 ---")
        result = self.make_request("GET", "/api/v1/cost/summary/page", params=params)
        test_case = {
            "name": "成本汇总查询",
            "endpoint": "GET /api/v1/cost/summary/page",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 成本汇总查询成功")
            page_data = self.safe_get(result, "data", "data", default={})
            records = page_data.get('records', [])
            print(f"  - 返回记录数: {len(records)}")
            print(f"  - 总记录数: {page_data.get('total', 0)}")
            if records:
                print(f"  - 首条记录: {records[0].get('productName', 'N/A')}")
            test_case["data"] = {"records": records[:5], "total": page_data.get('total', 0)}
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 成本汇总查询失败: {error_msg}")
            test_case["error"] = error_msg
        module_results["tests"].append(test_case)

        # 3.2 标准成本查询
        print("\n--- 3.2 标准成本查询 ---")
        result = self.make_request("GET", "/api/v1/cost/standard/page", params=params)
        test_case = {
            "name": "标准成本查询",
            "endpoint": "GET /api/v1/cost/standard/page",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 标准成本查询成功")
            page_data = self.safe_get(result, "data", "data", default={})
            records = page_data.get('records', [])
            print(f"  - 返回记录数: {len(records)}")
            print(f"  - 总记录数: {page_data.get('total', 0)}")
            if records:
                print(f"  - 首条记录产品: {records[0].get('productName', 'N/A')}, 标准成本: {records[0].get('standardCost', 0)}")
            test_case["data"] = {"records": records[:5], "total": page_data.get('total', 0)}
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 标准成本查询失败: {error_msg}")
            test_case["error"] = error_msg
        module_results["tests"].append(test_case)

        # 3.3 成本差异分析
        print("\n--- 3.3 成本差异分析 ---")
        result = self.make_request("GET", "/api/v1/cost/variance/page", params=params)
        test_case = {
            "name": "成本差异分析",
            "endpoint": "GET /api/v1/cost/variance/page",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 成本差异分析查询成功")
            page_data = self.safe_get(result, "data", "data", default={})
            records = page_data.get('records', [])
            print(f"  - 返回记录数: {len(records)}")
            print(f"  - 总记录数: {page_data.get('total', 0)}")
            if records:
                variance = records[0]
                print(f"  - 首条: {variance.get('productName', 'N/A')}, 差异: {variance.get('varianceAmount', 0)}")
            test_case["data"] = {"records": records[:5], "total": page_data.get('total', 0)}
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 成本差异分析查询失败: {error_msg}")
            test_case["error"] = error_msg
        module_results["tests"].append(test_case)

        self.test_results.append(module_results)
        return module_results

    def test_production_report(self):
        """测试生产报表模块"""
        print("\n" + "=" * 60)
        print("4. 生产报表模块测试")
        print("=" * 60)

        module_results = {"module": "生产报表", "tests": []}

        # 4.1 生产日报统计 (生产效率报表)
        print("\n--- 4.1 生产日报-生产效率报表 ---")
        params = {"pageNum": 1, "pageSize": 10, "startDate": "2026-01-01", "endDate": "2026-03-30"}
        result = self.make_request("GET", "/api/v1/report/production/efficiency", params=params)
        test_case = {
            "name": "生产日报-生产效率报表",
            "endpoint": "GET /api/v1/report/production/efficiency",
            "params": params,
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 生产效率报表查询成功")
            page_data = self.safe_get(result, "data", "data", default={})
            records = page_data.get('records', []) or page_data.get('list', [])
            print(f"  - 返回记录数: {len(records)}")
            print(f"  - 总记录数: {page_data.get('total', 0)}")
            if records:
                print(f"  - 首条: 工厂{records[0].get('factoryId', 'N/A')}, 效率: {records[0].get('efficiency', 0)}%")
            test_case["data"] = {"records": records[:5], "total": page_data.get('total', 0)}
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 生产效率报表查询失败: {error_msg}")
            test_case["error"] = error_msg
        module_results["tests"].append(test_case)

        # 4.2 生产看板统计
        print("\n--- 4.2 生产看板-统计 ---")
        result = self.make_request("GET", "/api/v1/api/production/dashboard/statistics")
        test_case = {
            "name": "生产看板-统计",
            "endpoint": "GET /api/v1/api/production/dashboard/statistics",
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 生产看板统计查询成功")
            stats_data = self.safe_get(result, "data", "data", default={})
            print(f"  - 待生产订单: {stats_data.get('pendingOrders', 0)}")
            print(f"  - 生产中订单: {stats_data.get('producingOrders', 0)}")
            print(f"  - 已完成订单: {stats_data.get('completedOrders', 0)}")
            test_case["data"] = stats_data
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 生产看板统计查询失败: {error_msg}")
            test_case["error"] = error_msg
        module_results["tests"].append(test_case)

        # 4.3 生产看板订单列表
        print("\n--- 4.3 生产看板-订单列表 ---")
        result = self.make_request("GET", "/api/v1/api/production/dashboard/orders")
        test_case = {
            "name": "生产看板-订单列表",
            "endpoint": "GET /api/v1/api/production/dashboard/orders",
            "passed": False,
            "result": str(result)[:500]
        }
        if result["success"] and self.safe_get(result, "data", "code") == 200:
            test_case["passed"] = True
            print(f"✓ 生产看板订单列表查询成功")
            orders_data = self.safe_get(result, "data", "data", default={})
            orders = orders_data.get('orders', [])
            print(f"  - 返回订单数: {len(orders)}")
            if orders:
                print(f"  - TOP1订单: {orders[0].get('orderNo', 'N/A')}, 状态: {orders[0].get('status', 'N/A')}")
            test_case["data"] = {"orders": orders[:5]}
        else:
            error_msg = result.get("error", self.safe_get(result, "data", "msg", default="Unknown"))
            print(f"✗ 生产看板订单列表查询失败: {error_msg}")
            test_case["error"] = error_msg
        module_results["tests"].append(test_case)

        self.test_results.append(module_results)
        return module_results

    def run_all_tests(self):
        """运行所有测试"""
        print("\n" + "=" * 60)
        print("多多进销存系统 - 报表统计模块测试")
        print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("=" * 60)

        # 登录
        if not self.login():
            print("登录失败，测试终止")
            return None

        # 测试各模块
        self.test_profit_analysis()
        self.test_cost_report()
        self.test_production_report()

        # 汇总结果
        self.print_summary()

        return self.test_results

    def print_summary(self):
        """打印测试汇总"""
        print("\n" + "=" * 60)
        print("测试结果汇总")
        print("=" * 60)

        total_tests = 0
        passed_tests = 0
        failed_tests = 0

        for module in self.test_results:
            module_name = module["module"]
            module_tests = module["tests"]
            module_passed = sum(1 for t in module_tests if t["passed"])
            module_total = len(module_tests)

            total_tests += module_total
            passed_tests += module_passed
            failed_tests += module_total - module_passed

            status = "✓" if module_passed == module_total else "⚠"
            print(f"{status} {module_name}: {module_passed}/{module_total} 通过")

        print(f"\n总计: {passed_tests}/{total_tests} 通过, {failed_tests} 失败")

        # 计算通过率
        pass_rate = (passed_tests / total_tests * 100) if total_tests > 0 else 0
        print(f"通过率: {pass_rate:.1f}%")

if __name__ == "__main__":
    tester = ReportTester()
    results = tester.run_all_tests()

    # 保存测试结果
    if results:
        with open("/Users/zxh/mycoding/duoduo_jxc/backend/test_report_results.json", "w", encoding="utf-8") as f:
            json.dump(results, f, ensure_ascii=False, indent=2)
        print("\n测试结果已保存到: /Users/zxh/mycoding/duoduo_jxc/backend/test_report_results.json")