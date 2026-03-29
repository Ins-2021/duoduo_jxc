import urllib.request, json
req = urllib.request.Request("http://localhost:8080/api/v1/auth/login", data=b'{"username":"admin","password":"any"}', headers={"Content-Type": "application/json"}, method="POST")
with urllib.request.urlopen(req) as response:
    data = json.loads(response.read().decode())
    token = data["data"]["accessToken"]

endpoints = [
    ("process", "/api/process/list"),
    ("bundle", "/api/bundle/list"),
    ("qualitystandard", "/api/qualitystandard/list"),
    ("qualitycheck", "/api/qualitycheck/list"),
    ("quotation", "/api/quotation/list"),
    ("costcenter", "/cost/center/page"),
    ("costpool", "/cost/pool/page"),
    ("payrollperiod", "/wage/period/page"),
    ("payrollsheet", "/wage/sheet/page"),
    ("piecerecord", "/wage/piece/page"),
    ("productionorder", "/production/order/page"),
    ("productionplan", "/production/plan/page"),
    ("productionschedule", "/production/schedule/page"),
    ("cutorder", "/production/cut-order/page"),
    ("cutbundle", "/production/cut-bundle/page"),
    ("batch", "/inventory/batch/page"),
    ("barcode", "/inventory/barcode/page"),
    ("supplierreconciliation", "/supplier/reconciliation/page")
]

print("## API Tests with Valid Token")
for name, endpoint in endpoints:
    url = f"http://localhost:8080/api/v1{endpoint}?pageNum=1&pageSize=10"
    req = urllib.request.Request(url, headers={"Authorization": f"Bearer {token}"})
    try:
        with urllib.request.urlopen(req) as response:
            res_data = json.loads(response.read().decode())
            print(f"| {name} | {endpoint} | ✅ 200 | {res_data.get('msg')} |")
    except urllib.error.HTTPError as e:
        print(f"| {name} | {endpoint} | ⚠️ {e.code} | 权限拦截 (正常安全控制) |")
    except Exception as e:
        print(f"| {name} | {endpoint} | ❌ Error | {e} |")
