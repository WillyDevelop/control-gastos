import os
import re

def create_getter_setter(type_name, var_name):
    cap_var = var_name[0].upper() + var_name[1:]
    getter_prefix = "is" if type_name.lower() == "boolean" else "get"
    getter = f"    public {type_name} {getter_prefix}{cap_var}() {{\n        return {var_name};\n    }}\n"
    setter = f"    public void set{cap_var}({type_name} {var_name}) {{\n        this.{var_name} = {var_name};\n    }}\n"
    return getter + "\n" + setter

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
        
    if '@Data' not in content and '@Getter' not in content and '@Setter' not in content and 'lombok' not in content:
        return
        
    lines = content.split('\n')
    new_lines = []
    fields = []
    
    for line in lines:
        if any(x in line for x in ['@Data', '@NoArgsConstructor', '@AllArgsConstructor', '@RequiredArgsConstructor', '@Builder']):
            continue
        if line.strip().startswith('import lombok.'):
            continue
            
        m = re.search(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([A-Za-z0-9_]+)\s*(?:=|;)', line)
        if m and '@NonNull' not in line:
            fields.append((m.group(1), m.group(2)))
        elif '@NonNull' in line:
            m2 = re.search(r'private\s+([A-Za-z0-9_<>\[\]]+)\s+([A-Za-z0-9_]+)\s*(?:=|;)', line)
            if m2:
                fields.append((m2.group(1), m2.group(2)))
            line = line.replace('@NonNull ', '')
            
        new_lines.append(line)
        
    methods = "\n"
    for t, v in fields:
        methods += create_getter_setter(t, v)
        
    final_content = '\n'.join(new_lines)
    idx = final_content.rfind('}')
    if idx != -1:
        final_content = final_content[:idx] + methods + final_content[idx:]
        
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(final_content)

for root, _, files in os.walk('src/main/java'):
    for f in files:
        if f.endswith('.java'):
            process_file(os.path.join(root, f))
