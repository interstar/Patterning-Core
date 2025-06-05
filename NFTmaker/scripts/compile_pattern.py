#!/usr/bin/env python3

import os
import sys
import shutil
import subprocess
import argparse
from pathlib import Path
import re

def ensure_dirs():
    """Ensure required directories exist"""
    os.makedirs("NFTmaker/patterns", exist_ok=True)
    os.makedirs("NFTmaker/dist/patterns", exist_ok=True)
    os.makedirs("target/pattern-build", exist_ok=True)

def ensure_dir(directory):
    """Ensure a directory exists, creating it if necessary."""
    Path(directory).mkdir(parents=True, exist_ok=True)

def copy_js_files(pattern_dir):
    """Copy FX(hash) library and random generator to pattern directory."""
    ensure_dir(pattern_dir)
    
    # Copy FX(hash) library
    fxhash_src = Path("NFTmaker/scripts/fxhash.min.js")
    if not fxhash_src.exists():
        print(f"Error: fxhash.min.js not found at {fxhash_src}")
        return False
    
    # Copy random generator
    random_gen_src = Path("NFTmaker/scripts/fxhash_random_generator.js")
    if not random_gen_src.exists():
        print(f"Error: fxhash_random_generator.js not found at {random_gen_src}")
        return False
    
    try:
        shutil.copy2(fxhash_src, pattern_dir / "fxhash.min.js")
        shutil.copy2(random_gen_src, pattern_dir / "fxhash_random_generator.js")
        return True
    except Exception as e:
        print(f"Error copying FX(hash) files: {str(e)}")
        return False

def compile_pattern(pattern_file, pattern_name):
    """Compile a pattern file into a standalone JS bundle"""
    # Ensure directories exist
    ensure_dirs()
    
    # Create pattern directory if it doesn't exist
    pattern_dir = Path("NFTmaker/patterns")
    pattern_dir.mkdir(exist_ok=True)
    
    # Create pattern-specific output directory
    pattern_output_dir = Path(f"NFTmaker/dist/patterns/{pattern_name}")
    pattern_output_dir.mkdir(parents=True, exist_ok=True)
    
    # Get the source and destination paths
    source_path = Path(pattern_file)
    if not source_path.suffix:
        source_path = source_path.with_suffix('.cljs')
    
    dest_path = pattern_dir / f"{pattern_name}.cljs"
    
    # Only copy if the file is not already in the patterns directory
    if source_path.parent != pattern_dir:
        shutil.copy2(source_path, dest_path)
        should_cleanup = True
    else:
        should_cleanup = False
    
    try:
        # Set up environment with pattern name
        env = os.environ.copy()
        env["PATTERN_NAME"] = pattern_name
        
        # Run lein cljsbuild
        try:
            # First clean any previous builds
            subprocess.run(["lein", "clean"], check=True, env=env)
            
            # Run the pattern build
            pattern_result = subprocess.run(["lein", "cljsbuild", "once", "pattern"], 
                                  check=True,
                                  capture_output=True,
                                  text=True,
                                  env=env)
            
            # If compilation succeeded, copy the JS file to pattern-specific directory
            js_file = pattern_output_dir / f"{pattern_name}.js"
            
            if js_file.exists():
                # Read the HTML template
                template_path = Path(os.path.dirname(os.path.abspath(__file__))) / "pattern_template.html"
                
                if not template_path.exists():
                    print(f"Error: HTML template not found at {template_path}")
                    return False
                
                with open(template_path, 'r') as f:
                    html_content = f.read()
                
                # Update the script src in the HTML to point to the correct JS file
                html_content = html_content.replace('src="{{pattern_name}}.js"', f'src="{pattern_name}.js"')
                
                # Update the script block to use the correct pattern name
                html_content = html_content.replace('window["{{pattern_name}}"]', f'window["{pattern_name}"]')
                
                # Write the HTML file to pattern-specific directory
                html_file = pattern_output_dir / "index.html"
                
                try:
                    with open(html_file, 'w') as f:
                        f.write(html_content)
                except Exception as e:
                    print(f"Error writing HTML file: {str(e)}")
                    return False
                
                # Copy FX(hash) files to pattern-specific directory
                if not copy_js_files(pattern_output_dir):
                    return False
                
                # Clean up build directory
                build_dir = pattern_output_dir / "build"
                if build_dir.exists():
                    shutil.rmtree(build_dir)
                
                print(f"\nPattern compiled successfully!")
                print(f"Output files created in: {pattern_output_dir}/")
                print(f"To view the pattern, run: cd {pattern_output_dir} && python3 -m http.server")
                return True
            else:
                print(f"Error: Compiled JS file not found at {js_file}")
                return False
            
        except subprocess.CalledProcessError as e:
            print(f"Error compiling pattern:")
            print(e.stdout)
            print(e.stderr)
            sys.exit(1)
            
    finally:
        # Clean up pattern file only if we copied it
        if should_cleanup and dest_path.exists():
            dest_path.unlink()

def main():
    parser = argparse.ArgumentParser(description="Compile a Patterning pattern into a standalone JS bundle")
    parser.add_argument("pattern_file", help="Path to the pattern file")
    parser.add_argument("--name", help="Name for the pattern (defaults to filename without extension)")
    
    args = parser.parse_args()
    
    # Get pattern name from filename if not provided
    pattern_name = args.name or Path(args.pattern_file).stem
    
    success = compile_pattern(args.pattern_file, pattern_name)
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main() 