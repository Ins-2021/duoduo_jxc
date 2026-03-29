import urllib.request, json
req = urllib.request.Request("http://localhost:8080/api/v1/auth/login", data=b'{"username":"admin","password":"any"}', headers={"Content-Type": "application/json"}, method="POST")
with urllib.request.urlopen(req) as response:
    data = json.loads(response.read().decode())
    token = data["data"]["accessToken"]

paths = ["process", "bundle", "qualitystandard", "qualitycheck", "quotation", "costcenter", "costpool", "wage/period", "wage/sheet", "piecerecord", "productionorder", "productionplan", "productionschedule", "cutorder", "cutbundle", "batch", "barcode", "supplierreconciliation"]

for path in paths:
    req = urllib.request.Request(f"http://localhost:8080/api/v1/{path}/page?pageNum=1&pageSize=10", headers={"Authorization": f"Bearer {token}"})
    try:
        with urllib.request.urlopen(req) as response:
            res_data = json.loads(response.read().decode())
            print(f"=== {path} ===")
            print(f"Code: {res_data.get('code')}")
    except urllib.error.HTTPError as e:
        print(f"=== {path} ===")
        print(f"Error: {e.code}")
    except Exception as e:
        print(f"=== {path} ===")
        print(f"Error: {e}")
