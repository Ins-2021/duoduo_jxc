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
        ('客户列表', f'{BASE}/api/v1/customer/page'),
        ('供应商列表', f'{BASE}/api/v1/supplier/page'),
        ('商品列表', f'{BASE}/api/v1/product/page'),
        ('仓库列表', f'{BASE}/api/v1/warehouse/page'),
        ('销售订单', f'{BASE}/api/v1/sales/page'),
        ('采购订单', f'{BASE}/api/v1/purchase/page'),
        ('生产订单', f'{BASE}/api/v1/production/order/page'),
        ('工序列表', f'{BASE}/api/v1/process/list'),
        ('计件记录', f'{BASE}/api/v1/processes/records/list'),
        ('工资单', f'{BASE}/api/v1/wage/sheet/page'),
        ('收款单', f'{BASE}/api/v1/finance/receipt/page', 'POST'),
        ('付款单', f'{BASE}/api/v1/finance/payment/page', 'POST'),
    ]

    ok_all = True
    print("=== 第五轮全模块API健康检查 ===")
    for item in checks:
        name = item[0]
        url = item[1]
        method = item[2] if len(item) > 2 else 'GET'
        
        try:
            if method == 'GET':
                res = get_json(url, token)
            else:
                req = urllib.request.Request(url, data=b'{}', headers={'Authorization': f'Bearer {token}', 'Content-Type': 'application/json'}, method='POST')
                with urllib.request.urlopen(req, timeout=10) as resp:
                    res = json.loads(resp.read().decode('utf-8'))
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

if __name__ == '__main__':
    main()