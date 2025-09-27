#!/usr/bin/env python3

import re
import markdown
import os
import subprocess
import sys
import tempfile
from jinja2 import Template

# Template for the HTML page
HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{ title }}</title>
    
    <!-- Preload fonts -->
    <link rel="preload" href="../../media/Analecta/Analecta.otf" as="font" type="font/otf" crossorigin>
    
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Crimson+Pro:ital,wght@0,400;0,600;1,400&family=JetBrains+Mono:wght@400;600&display=swap" rel="stylesheet">

    <!-- Site styles -->
    <link rel="stylesheet" href="../../css/article_styles.css">
    <link rel="stylesheet" href="../../css/sketch_styles.css">
    
    <!-- CodeMirror CSS -->
    <link rel="stylesheet" href="css/codemirror.min.css">
    <link rel="stylesheet" href="css/codemirror/theme/dracula.css">
    <!-- Embedded editor CSS -->
    <link rel="stylesheet" href="css/embedded-editor.css">
    
    <style>
        /* Top bar styles */
        .top-bar {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            height: 40px;
            background: var(--color-dark);
            display: flex;
            align-items: center;
            padding: 0 2rem;
            z-index: 1000;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .top-bar a {
            font-family: 'Analecta', serif;
            color: var(--color-background);
            text-decoration: none;
            font-size: 1.2rem;
            letter-spacing: 0.05em;
        }
        
        .top-bar a:hover {
            color: var(--color-accent);
        }
        
        /* Adjust layout to account for fixed top bar */
        .layout {
            margin-top: 40px;
        }
        
        /* Pattern example styles */
        .pattern-example {
            margin: 2em 0;
            padding: 1em;
            border: 1px solid #eee;
            border-radius: 4px;
            background: #fafafa;
        }
        
        .pattern-code {
            margin: 1em 0;
            background: #2d3748;
            border-radius: 4px;
            overflow: hidden;
        }
        
        .pattern-code pre {
            margin: 0;
            padding: 1em;
            color: #e2e8f0;
            font-family: 'JetBrains Mono', monospace;
            font-size: 0.9em;
            line-height: 1.4;
            overflow-x: auto;
        }
        
        .pattern-preview {
            margin: 1em 0;
            text-align: center;
            background: white;
            border-radius: 4px;
            padding: 1em;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .pattern-preview img {
            max-width: 100%;
            height: auto;
            max-height: 400px;
        }
        
        /* Pattern action buttons */
        .pattern-actions {
            display: flex;
            gap: 0.5em;
            padding: 0.5em;
            background: #f5f5f5;
            border-top: 1px solid #ddd;
            align-items: center;
            justify-content: center;
        }
        
        .pattern-actions button {
            padding: 0.5em 1em;
            font-size: 0.9em;
            font-family: 'JetBrains Mono', monospace;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            transition: background 0.2s;
            height: 36px;
            line-height: 1;
        }
        
        .pattern-actions button.copy {
            background: #2c5282;
            color: white;
        }
        
        .pattern-actions button.copy:hover {
            background: #2b6cb0;
        }
        
        .pattern-actions button.download {
            background: #2f855a;
            color: white;
        }
        
        .pattern-actions button.download:hover {
            background: #38a169;
        }
        
        .pattern-actions button.workbench {
            background: #805ad5;
            color: white;
        }
        
        .pattern-actions button.workbench:hover {
            background: #9f7aea;
        }
    </style>
</head>
<body>
    <div class="top-bar">
        <a href="../../index.html">Alchemy Islands</a>
    </div>
    <div class="layout">
        <div class="content">
            <div class="article-content">
                {{ content|safe }}
            </div>
        </div>
    </div>
    
    <!-- Pattern definitions -->
    <script>
        const patterns = {};
        {% for pattern in patterns %}
        patterns['pattern{{ loop.index }}'] = {{ pattern | tojson }};
        {% endfor %}
        
        // Action functions
        function copyCode(patternId) {
            const code = patterns[patternId];
            
            // Try modern clipboard API first
            if (navigator.clipboard && window.isSecureContext) {
                navigator.clipboard.writeText(code).then(function() {
                    showCopySuccess(event.target);
                }).catch(function(err) {
                    console.error('Clipboard API failed: ', err);
                    fallbackCopyToClipboard(code, event.target);
                });
            } else {
                // Fallback for HTTP or older browsers
                fallbackCopyToClipboard(code, event.target);
            }
        }
        
        function fallbackCopyToClipboard(text, button) {
            // Create a temporary textarea
            const textArea = document.createElement('textarea');
            textArea.value = text;
            textArea.style.position = 'fixed';
            textArea.style.left = '-999999px';
            textArea.style.top = '-999999px';
            document.body.appendChild(textArea);
            textArea.focus();
            textArea.select();
            
            try {
                const successful = document.execCommand('copy');
                if (successful) {
                    showCopySuccess(button);
                } else {
                    showCopyError(button);
                }
            } catch (err) {
                console.error('Fallback copy failed: ', err);
                showCopyError(button);
            }
            
            document.body.removeChild(textArea);
        }
        
        function showCopySuccess(button) {
            const originalText = button.textContent;
            button.textContent = 'Copied!';
            button.style.background = '#2f855a';
            setTimeout(function() {
                button.textContent = originalText;
                button.style.background = '#2c5282';
            }, 1000);
        }
        
        function showCopyError(button) {
            const originalText = button.textContent;
            button.textContent = 'Copy Failed';
            button.style.background = '#e53e3e';
            setTimeout(function() {
                button.textContent = originalText;
                button.style.background = '#2c5282';
            }, 2000);
        }
        
        function downloadSVG(patternId) {
            const svgElement = document.querySelector(`#${patternId}-preview svg`);
            if (svgElement) {
                const svgData = new XMLSerializer().serializeToString(svgElement);
                const svgBlob = new Blob([svgData], {type: 'image/svg+xml;charset=utf-8'});
                const svgUrl = URL.createObjectURL(svgBlob);
                const downloadLink = document.createElement('a');
                downloadLink.href = svgUrl;
                downloadLink.download = `pattern-${patternId}.svg`;
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink);
                URL.revokeObjectURL(svgUrl);
            }
        }
        
        function openInWorkbench(patternId) {
            const code = patterns[patternId];
            
            // Check if URL would be too long (browsers typically limit to ~2000 chars)
            const encodedCode = encodeURIComponent(code);
            const fullUrl = `workbench/index.html?code=${encodedCode}`;
            
            if (fullUrl.length > 2000) {
                // Use localStorage for long code
                const storageKey = 'patterning_workbench_code_' + Date.now();
                localStorage.setItem(storageKey, code);
                window.open(`workbench/index.html?storageKey=${storageKey}`, '_blank');
            } else {
                // Use URL parameter for short code
                window.open(fullUrl, '_blank');
            }
        }
    </script>
</body>
</html>
"""

def convert_wiki_links(content):
    """Convert wiki-style links [[Page Name]] to HTML links."""
    # Pattern to match [[Page Name]] but not inside code blocks
    pattern = r'\[\[([^\]]+)\]\]'
    
    def replace_link(match):
        page_name = match.group(1)
        return f'<a href="{page_name}.html">{page_name}</a>'
    
    return re.sub(pattern, replace_link, content)

def generate_svg_for_pattern(pattern_code, pattern_id, output_dir, page_name):
    """Generate SVG for a pattern using the Patterning CLI tool."""
    try:
        # Create temporary file for pattern code
        with tempfile.NamedTemporaryFile(mode='w', suffix='.clj', delete=False) as temp_file:
            temp_file.write(pattern_code)
            temp_file_path = temp_file.name
        
        # Generate SVG using CLI tool with page name prefix
        svg_path = os.path.join(output_dir, f'{page_name}-pattern-{pattern_id}.svg')
        
        # Run the CLI tool from the patterning-core root directory
        # Since we're now running from the root, we can use current directory
        cwd = '.'
        
        cmd = ['lein', 'run', '-m', 'patterning.cli', temp_file_path, svg_path, 'svg', '400', '400']
        print(f"DEBUG: Running command: {' '.join(cmd)}")
        print(f"DEBUG: Working directory: {cwd}")
        print(f"DEBUG: Target SVG path: {svg_path}")
            
        result = subprocess.run(cmd, capture_output=True, text=True, cwd=cwd)
        
        print(f"DEBUG: Command return code: {result.returncode}")
        print(f"DEBUG: Command stdout: {result.stdout}")
        print(f"DEBUG: Command stderr: {result.stderr}")
        
        if result.returncode == 0:
            print(f"Generated SVG: {svg_path}")
            print(f"DEBUG: SVG file exists after generation: {os.path.exists(svg_path)}")
            if os.path.exists(svg_path):
                print(f"DEBUG: SVG file size: {os.path.getsize(svg_path)} bytes")
            return svg_path
        else:
            print(f"Error generating SVG for pattern {pattern_id}. Halting build.")
            print(f"--- STDOUT ---\n{result.stdout}")
            print(f"--- STDERR ---\n{result.stderr}")
            sys.exit(1)
            
    except Exception as e:
        print(f"Error generating SVG for pattern {pattern_id}: {e}")
        return None
    finally:
        # Clean up temporary file
        try:
            os.unlink(temp_file_path)
        except:
            pass

def process_blocks(content, output_dir, page_name, tutorial_root):
    """Process content by splitting on hyphens and handling each block appropriately."""
    # Split on 4 or more hyphens
    blocks = re.split(r'-{4,}', content)
    
    # Process each block
    patterns = []
    markdown_blocks = []
    
    for block in blocks:
        block = block.strip()
        if not block:
            continue
            
        # Check if this is a pattern block
        lines = block.split('\n')
        if lines[0].strip() == ':patterning':
            # This is a pattern block - collect all the code
            pattern = '\n'.join(lines[1:]).strip()
            patterns.append(pattern)
            
            # Generate SVG for this pattern with page name prefix
            pattern_id = len(patterns)
            svg_path = generate_svg_for_pattern(pattern, pattern_id, output_dir, page_name)
            
            # Add a pattern example div with the new styling
            if svg_path:
                print(f"DEBUG: Attempting to read SVG file: {svg_path}")
                print(f"DEBUG: File exists: {os.path.exists(svg_path)}")
                if os.path.exists(svg_path):
                    print(f"DEBUG: File size: {os.path.getsize(svg_path)} bytes")
                try:
                    with open(svg_path, 'r') as f:
                        svg_content = f.read()
                    print(f"DEBUG: Successfully read SVG content, length: {len(svg_content)}")
                except IOError as e:
                    print(f"DEBUG: IOError reading SVG file: {e}")
                    svg_content = f"<p>Error reading SVG file: {e}</p>"
                except Exception as e:
                    print(f"DEBUG: Unexpected error reading SVG file: {e}")
                    svg_content = f"<p>Unexpected error reading SVG file: {e}</p>"
                container = f'''
<div class="pattern-example">
    <div class="pattern-code">
        <pre><code>{pattern}</code></pre>
    </div>
    <div class="pattern-preview" id="pattern-{pattern_id}-preview">
        {svg_content}
    </div>
    <div class="pattern-actions">
        <button class="copy" onclick="copyCode('pattern{pattern_id}')">Copy Code</button>
        <button class="download" onclick="downloadSVG('pattern-{pattern_id}')">Download SVG</button>
        <button class="workbench" onclick="openInWorkbench('pattern{pattern_id}')">Open in Workbench</button>
    </div>
</div>'''
            else:
                # Fallback if SVG generation fails
                container = f'''
<div class="pattern-example">
    <div class="pattern-code">
        <pre><code>{pattern}</code></pre>
    </div>
    <div class="pattern-preview">
        <p>Error generating pattern preview</p>
    </div>
    <div class="pattern-actions">
        <button class="copy" onclick="copyCode('pattern{pattern_id}')">Copy Code</button>
        <button class="workbench" onclick="openInWorkbench('pattern{pattern_id}')">Open in Workbench</button>
    </div>
</div>'''
            
            markdown_blocks.append(container)
        else:
            # This is a markdown block - keep it as is
            markdown_blocks.append(block)
    
    # Join the markdown blocks back together
    markdown_content = '\n\n'.join(markdown_blocks)
    
    # Convert wiki-style links first
    markdown_content = convert_wiki_links(markdown_content)
    
    # Configure markdown converter
    md = markdown.Markdown(extensions=['extra'])
    
    # Convert markdown to HTML
    html_content = md.convert(markdown_content)
    
    return html_content, patterns

def generate_html_page(markdown_file, output_file, tutorial_root):
    """Generate HTML page from markdown file with embedded pattern examples."""
    # Read markdown content
    with open(markdown_file, 'r') as f:
        content = f.read()
    
    # Get title from first line of markdown (if it's a heading)
    title = "Pattern Tutorial"
    first_line = content.split('\n')[0]
    if first_line.startswith('# '):
        title = first_line[2:].strip()
    
    # Get page name from the output file name (without extension) and sanitize it
    page_name = os.path.splitext(os.path.basename(output_file))[0]
    # Remove spaces and other problematic characters for filenames, then convert to lowercase
    page_name = re.sub(r'[^\w\-_]', '_', page_name).lower()
    
    # Create output directory for SVGs
    output_dir = os.path.dirname(output_file)
    os.makedirs(output_dir, exist_ok=True)
    
    # Process content and extract patterns
    html_content, patterns = process_blocks(content, output_dir, page_name, tutorial_root)
    
    # Generate HTML using template
    template = Template(HTML_TEMPLATE)
    html = template.render(
        title=title,
        content=html_content,
        patterns=patterns
    )
    
    # Write output file
    with open(output_file, 'w') as f:
        f.write(html)

def main():
    import argparse
    parser = argparse.ArgumentParser(description='Generate HTML page with pattern editors from markdown')
    parser.add_argument('input_file', help='Input markdown file')
    parser.add_argument('output_file', help='Output HTML file')
    parser.add_argument('tutorial_root', help='Tutorial root path for SVG references')
    args = parser.parse_args()
    
    generate_html_page(args.input_file, args.output_file, args.tutorial_root)
    print(f"Generated {args.output_file} from {args.input_file}")

if __name__ == '__main__':
    main() 