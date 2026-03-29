import urllib.request, json
req = urllib.request.Request("http://localhost:8080/api/v1/auth/login", data=b'{"username":"admin","password":"any"}', headers={"Content-Type": "application/json"}, method="POST")
with urllib.request.urlopen(req) as response:
    data = json.loads(response.read().decode())
    token = data["data"]["accessToken"]

paths_with_api = ["process", "bundle", "qualitystandard", "qualitycheck", "quotation", "costcenter", "costpool", "piecerecord", "productionorder", "productionplan", "productionschedule", "cutorder", "cutbundle", "batch", "barcode", "supplierreconciliation"]

for path in paths_with_api:
    url = f"http://localhost:8080/api/v1/api/{path}/list?pageNum=1&pageSize=10"
    req = urllib.request.Request(url, headers={"Authorization": f"Bearer {token}"})
    try:
        with urllib.request.urlopen(req) as response:
            res_data = json.loads(response.read().decode())
            print(f"=== {path} ===")
            print(f"Code: {res_data.get('code')} - {res_data.get('msg')}")
    except urllib.error.HTTPError as e:
        print(f"=== {path} ===")
        print(f"Error: {e.code}")

# For wage modules
wage_paths = ["wage/period", "wage/sheet"]
for path in wage_paths:
    url = f"http://localhost:8080/api/v1/{path}/page?pageNum=1&pageSize=10"
    req = urllib.request.Request(url, headers={"Authorization": f"Bearer {token}"})
    try:
        with urllib.request.urlopen(req) as response:
            res_data = json.loads(response.read().decode())
            print(f"=== {path} ===")
            print(f"Code: {res_data.get('code')} - {res_data.get('msg')}")
    except urllib.error.HTTPError as e:
        print(f"=== {path} ===")
        try:
            err_data = json.loads(e.read().decode())
            print(f"Error: {e.code} - {err_data.get('msg')}")
        except:
            print(f"Error: {e.code}")
