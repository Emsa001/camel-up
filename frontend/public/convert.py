#!/usr/bin/env python3
"""
Image Format Converter Script
Converts all images to PNG format regardless of their original format.
"""

import os
import sys
from PIL import Image
import argparse


def convert_to_png(image_path, output_path=None, quality=95, preserve_transparency=True):
    """
    Convert an image to PNG format.
    
    Args:
        image_path (str): Path to the input image
        output_path (str): Path to save the PNG image (optional)
        quality (int): Quality for formats that support it (not used for PNG output)
        preserve_transparency (bool): Whether to preserve transparency
    
    Returns:
        bool: True if image was converted, False otherwise
    """
    try:
        # Open the image
        with Image.open(image_path) as img:
            original_format = img.format
            original_width, original_height = img.size
            
            print(f"Converting: {os.path.basename(image_path)}")
            print(f"  Original format: {original_format}")
            print(f"  Size: {original_width}x{original_height}")
            
            # Skip if already PNG
            if original_format == 'PNG' and image_path.lower().endswith('.png'):
                print(f"  Already PNG - skipped")
                return False
            
            # Determine output path
            if output_path is None:
                # Change extension to .png
                path_without_ext = os.path.splitext(image_path)[0]
                output_path = path_without_ext + '.png'
            
            # Handle different image modes for PNG conversion
            if preserve_transparency:
                # Keep transparency if it exists
                if img.mode in ('RGBA', 'LA'):
                    # Already has alpha channel
                    converted_img = img
                elif img.mode == 'P' and 'transparency' in img.info:
                    # Palette mode with transparency
                    converted_img = img.convert('RGBA')
                elif img.mode in ('RGB', 'L', 'CMYK'):
                    # No transparency, but we can add alpha channel if needed
                    converted_img = img
                else:
                    # Convert other modes to RGBA for maximum compatibility
                    converted_img = img.convert('RGBA')
            else:
                # Convert to RGB (no transparency)
                if img.mode in ('RGBA', 'LA'):
                    # Create white background for transparent images
                    background = Image.new('RGB', img.size, (255, 255, 255))
                    if img.mode == 'RGBA':
                        background.paste(img, mask=img.split()[-1])  # Use alpha channel as mask
                    else:
                        background.paste(img)
                    converted_img = background
                else:
                    converted_img = img.convert('RGB')
            
            # Save as PNG with optimization
            converted_img.save(output_path, 'PNG', optimize=True, compress_level=9)
            
            # Get file sizes
            original_size = os.path.getsize(image_path) / (1024 * 1024)
            new_size = os.path.getsize(output_path) / (1024 * 1024)
            
            print(f"  Converted to PNG: {original_size:.2f}MB → {new_size:.2f}MB")
            
            # Remove original file if it's different from output
            if output_path != image_path and os.path.exists(image_path):
                os.remove(image_path)
                print(f"  Removed original: {os.path.basename(image_path)}")
            
            return True
                
    except Exception as e:
        print(f"  Error converting {image_path}: {str(e)}")
        return False


def process_directory(directory_path, recursive=True, preserve_transparency=True):
    """
    Convert all images in a directory to PNG.
    
    Args:
        directory_path (str): Path to the directory
        recursive (bool): Whether to process subdirectories
        preserve_transparency (bool): Whether to preserve transparency
    """
    # Supported image extensions
    image_extensions = {'.jpg', '.jpeg', '.bmp', '.tiff', '.tif', '.webp', '.gif', '.ico', '.png'}
    
    processed_count = 0
    converted_count = 0
    
    print(f"Converting images to PNG in: {directory_path}")
    print("=" * 60)
    
    # Walk through directory
    for root, dirs, files in os.walk(directory_path):
        for file in files:
            file_path = os.path.join(root, file)
            file_ext = os.path.splitext(file)[1].lower()
            
            # Check if it's an image file
            if file_ext in image_extensions:
                processed_count += 1
                if convert_to_png(file_path, preserve_transparency=preserve_transparency):
                    converted_count += 1
                print()  # Empty line for readability
        
        # If not recursive, only process the first level
        if not recursive:
            break
    
    print("=" * 60)
    print(f"Conversion complete!")
    print(f"Images processed: {processed_count}")
    print(f"Images converted: {converted_count}")
    print(f"Images already PNG: {processed_count - converted_count}")


def main():
    """Main function to handle command line arguments and execute the script."""
    parser = argparse.ArgumentParser(description='Convert all images to PNG format')
    parser.add_argument('path', nargs='?', default='.', 
                       help='Path to directory or image file (default: current directory)')
    parser.add_argument('--no-recursive', action='store_true', 
                       help='Don\'t process subdirectories')
    parser.add_argument('--no-transparency', action='store_true',
                       help='Remove transparency (convert to RGB with white background)')
    parser.add_argument('--keep-originals', action='store_true',
                       help='Keep original files (create new PNG files instead of replacing)')
    
    args = parser.parse_args()
    
    # Check if PIL is available
    try:
        from PIL import Image
    except ImportError:
        print("Error: Pillow library is required. Install it with: pip install Pillow")
        sys.exit(1)
    
    # Validate path
    if not os.path.exists(args.path):
        print(f"Error: Path '{args.path}' does not exist.")
        sys.exit(1)
    
    # Set preserve transparency option
    preserve_transparency = not args.no_transparency
    
    # Process single file or directory
    if os.path.isfile(args.path):
        print("Converting single file...")
        if args.keep_originals:
            # Create new PNG file with _png suffix
            path_without_ext = os.path.splitext(args.path)[0]
            output_path = path_without_ext + '_png.png'
            convert_to_png(args.path, output_path, preserve_transparency=preserve_transparency)
        else:
            convert_to_png(args.path, preserve_transparency=preserve_transparency)
    elif os.path.isdir(args.path):
        if args.keep_originals:
            print("Warning: --keep-originals flag is not supported for directory processing.")
            print("Original files will be replaced with PNG versions.")
        process_directory(args.path, recursive=not args.no_recursive, preserve_transparency=preserve_transparency)
    else:
        print(f"Error: '{args.path}' is not a valid file or directory.")
        sys.exit(1)


if __name__ == "__main__":
    main()