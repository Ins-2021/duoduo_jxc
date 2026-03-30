import os

api_dir = 'frontend/src/api'

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    idx = 0
    while True:
        idx = content.find('request({', idx)
        if idx == -1:
            break
            
        # find matching brace
        brace_count = 1
        i = idx + 9
        while i < len(content) and brace_count > 0:
            if content[i] == '{':
                brace_count += 1
            elif content[i] == '}':
                brace_count -= 1
            i += 1
            
        if brace_count == 0:
            block = content[idx:i]
            # Check for standard url property patterns
            if not any(pattern in block for pattern in ['url:', 'url :', '\n  url,', '\n    url,']):
                # Find the function name
                func_idx = content.rfind('function ', 0, idx)
                if func_idx != -1:
                    func_end = content.find('(', func_idx)
                    func_name = content[func_idx+9:func_end].strip()
                else:
                    const_idx = content.rfind('const ', 0, idx)
                    if const_idx != -1:
                        func_end = content.find('=', const_idx)
                        func_name = content[const_idx+6:func_end].strip()
                    else:
                        func_name = "Unknown"
                        
                print(f"File: {filepath} | Function: {func_name}")
                print(f"Block:\n{block}\n")
        
        idx = i

for root, dirs, files in os.walk(api_dir):
    for f in files:
        if f.endswith('.ts') or f.endswith('.js'):
            process_file(os.path.join(root, f))
