#!/usr/bin/env python3

import os
import sys
import subprocess
from pathlib import Path
import markdown
import re
from bs4 import BeautifulSoup

def ensure_dirs():
    """Ensure required directories exist"""
    os.makedirs("presentation/slides", exist_ok=True)

def convert_markdown_to_reveal(markdown_content):
    """Convert markdown content to Reveal.js HTML"""
    html = markdown.markdown(markdown_content)
    return f'<section>{html}</section>'

def generate_pattern_slide(pattern_name):
    """Generate a pattern slide using compile_slide.py"""
    pattern_file = f"patterns/{pattern_name}"
    if not os.path.exists(pattern_file):
        print(f"Error: Pattern file not found: {pattern_file}")
        return None
    result = subprocess.run(
        ["python", "compile_slide.py", pattern_file],
        capture_output=True,
        text=True
    )
    if result.returncode != 0:
        print(f"Error compiling pattern {pattern_name}:")
        print(result.stderr)
        return None
    return Path(pattern_name).stem

def process_markdown_presentation(md_file):
    ensure_dirs()
    with open(md_file, 'r') as f:
        content = f.read()
    sections = content.split('----')
    slides = []
    for section in sections:
        section = section.strip()
        if not section:
            continue
        if ':PATTERN' in section:
            pattern_name = section.strip().split(':PATTERN')[1].strip()
            pattern_slide = generate_pattern_slide(pattern_name)
            if pattern_slide:
                iframe_slide = f'<section data-background-iframe="{pattern_slide}.html" data-background-interactive></section>'
                slides.append(iframe_slide)
        else:
            slide = convert_markdown_to_reveal(section)
            slides.append(slide)
    generate_final_presentation(slides)

def generate_final_presentation(slides):
    template_path = Path("slide_template.html")
    with open(template_path, 'r') as f:
        template = f.read()
    soup = BeautifulSoup(template, 'html.parser')
    slides_div = soup.find('div', class_='slides')
    slides_div.clear()
    for slide in slides:
        slide_soup = BeautifulSoup(slide, 'html.parser')
        slides_div.append(slide_soup)
    output_file = "slides/presentation.html"
    with open(output_file, 'w') as f:
        f.write(str(soup))
    print(f"Presentation generated: {output_file}")

def main():
    if len(sys.argv) != 2:
        print("Usage: python compile_presentation.py <markdown_file>")
        sys.exit(1)
    md_file = sys.argv[1]
    if not os.path.exists(md_file):
        print(f"Error: Markdown file not found: {md_file}")
        sys.exit(1)
    process_markdown_presentation(md_file)

if __name__ == "__main__":
    main() 