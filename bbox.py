from PIL import Image

def print_bbox(path):
    try:
        img = Image.open(path)
        bbox = img.getbbox()
        print(f"{path}: Size={img.size}, Bbox={bbox}, Visual Width={bbox[2]-bbox[0] if bbox else 0}")
    except Exception as e:
        print(f"Error reading {path}: {e}")

print_bbox("assets/classrooom/EmptyStudentChairtable.png")
print_bbox("assets/classrooom/StudentTablechair.png")
print_bbox("assets/classrooom/TeacherDesk.png")
print_bbox("assets/LibraryTable.png")
