#!/usr/bin/env python3

import os
import sys
import subprocess
from pathlib import Path
import markdown
import re
from bs4 import BeautifulSoup
import argparse

def convert_markdown_to_reveal(markdown_content):
    """Convert markdown content to Reveal.js HTML"""
    html = markdown.markdown(markdown_content)
    return f'<section>{html}</section>'

def generate_pattern_slide(pattern_name, no_compile=False):
    """Generate a pattern slide using compile_slide.py"""
    # Always use patterns/ subdir for pattern files
    print("In generate_pattern_slide")
    if not pattern_name.startswith("patterns/"):
        pattern_file = f"patterns/{pattern_name}"
    else:
        pattern_file = pattern_name
    if not os.path.exists(pattern_file):
        print(f"Error: Pattern file not found: {pattern_file}")
        return None
    print("About to run compile_slide on %s" % pattern_file)
    cmd = [sys.executable, "compile_slide.py", pattern_file]
    if no_compile:
        cmd.append("--no-compile")
    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True
    )
    print(result.stdout)
    print(result.stderr)
    if result.returncode != 0:
        print(f"Error compiling pattern {pattern_name}:")
        print(result.stderr)
        return None
    return Path(pattern_file).stem

def process_markdown_presentation(md_file, no_compile=False):
    print("In process_markdown_presentation")
    # No need to create extra directories, just ensure slides/ exists
    os.makedirs("slides", exist_ok=True)
    with open(md_file, 'r') as f:
        content = f.read()
    sections = content.split('----')
    slides = []
    for section in sections:
        section = section.strip()
        print(f"Processing section: {section[:40]}")
        if not section:
            continue
        if ':PATTERN' in section:
            print("Found a PATTERN section!")
            pattern_name = section.strip().split(':PATTERN')[1].strip()
            pattern_slide = generate_pattern_slide(pattern_name, no_compile=no_compile)
            if pattern_slide:
                iframe_slide = (
                    f'<section data-background-iframe="{pattern_slide}.html" data-background-interactive>'
                    f'<a href="{pattern_slide}.html" target="_blank" '
                    f'style="position:absolute;top:10px;right:20px;z-index:10;font-size:1.2em;">➡️</a>'
                    f'</section>'
                )
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
    """Main function"""
    parser = argparse.ArgumentParser(description="Compile a Patterning markdown presentation into Reveal.js slides")
    parser.add_argument("markdown_file", help="Path to the markdown file")
    parser.add_argument("--no-compile", action="store_true", help="Skip ClojureScript compilation step for all patterns")
    args = parser.parse_args()
    if not os.path.exists(args.markdown_file):
        print(f"Error: Markdown file not found: {args.markdown_file}")
        sys.exit(1)
    process_markdown_presentation(args.markdown_file, no_compile=args.no_compile)

if __name__ == "__main__":
    main() 