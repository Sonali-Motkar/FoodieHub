import os
from PIL import Image, ImageDraw, ImageFont, ImageOps

font_dir = r'C:\Windows\Fonts'
f_cls = ImageFont.truetype(os.path.join(font_dir, 'arialbd.ttf'), 15)
f_msg = ImageFont.truetype(os.path.join(font_dir, 'arial.ttf'), 13)
f_box = ImageFont.truetype(os.path.join(font_dir, 'arial.ttf'), 14)
f_hdr = ImageFont.truetype(os.path.join(font_dir, 'arialbd.ttf'), 16)

box_fill = (245, 245, 245)
border_c = (90, 90, 90)
line_c = (50, 50, 50)
text_c = (0, 0, 0)

# ==============================================================
# 1. SEQUENCE DIAGRAM
# ==============================================================
w = 1100
h = 750
img = Image.new('RGB', (w, h), (255, 255, 255))
draw = ImageDraw.Draw(img)

cols = [
    ('Customer', 100, True),
    ('CartActivity', 320, False),
    ('DatabaseHelper', 540, False),
    ('ApiClient', 760, False),
    ('Node.js + MongoDB', 980, False)
]

for name, cx, is_actor in cols:
    if is_actor:
        draw.ellipse([cx - 10, 20, cx + 10, 40], fill=(235, 235, 235), outline=line_c, width=1)
        draw.line([cx, 40, cx, 65], fill=line_c, width=1)
        draw.line([cx - 14, 48, cx + 14, 48], fill=line_c, width=1)
        draw.line([cx, 65, cx - 10, 85], fill=line_c, width=1)
        draw.line([cx, 65, cx + 10, 85], fill=line_c, width=1)
        tb = draw.textbbox((0, 0), name, font=f_cls)
        tw = tb[2] - tb[0]
        draw.text((cx - tw // 2, 92), name, font=f_cls, fill=text_c)
    else:
        bw, bh = 170, 36
        draw.rectangle([cx - bw//2, 50, cx + bw//2, 50 + bh], fill=box_fill, outline=border_c, width=1)
        tb = draw.textbbox((0, 0), name, font=f_cls)
        tw = tb[2] - tb[0]
        th = tb[3] - tb[1]
        draw.text((cx - tw // 2, 50 + (bh - th) // 2 - 2), name, font=f_cls, fill=text_c)

for _, cx, _ in cols:
    for y in range(120, h - 30, 8):
        draw.line([cx, y, cx, y + 4], fill=(160, 160, 160), width=1)

def send_msg(x1, x2, y, text, dotted=False):
    if dotted:
        dx = x2 - x1
        steps = int(abs(dx) / 8)
        for i in range(steps):
            if i % 2 == 0:
                sx = x1 + (dx * i / steps)
                ex = x1 + (dx * (i + 1) / steps)
                draw.line([sx, y, ex, y], fill=line_c, width=1)
        dir_sign = 1 if x2 > x1 else -1
        draw.line([x2, y, x2 - dir_sign * 8, y - 4], fill=line_c, width=1)
        draw.line([x2, y, x2 - dir_sign * 8, y + 4], fill=line_c, width=1)
    else:
        draw.line([x1, y, x2, y], fill=line_c, width=1)
        dir_sign = 1 if x2 > x1 else -1
        draw.polygon([(x2, y), (x2 - dir_sign * 8, y - 4), (x2 - dir_sign * 8, y + 4)], fill=line_c)
        
    tb = draw.textbbox((0, 0), text, font=f_msg)
    tw = tb[2] - tb[0]
    draw.text(((x1 + x2)//2 - tw//2, y - 17), text, font=f_msg, fill=text_c)

send_msg(100, 320, 160, "Tap 'Proceed to Pay'")
send_msg(320, 540, 220, "getCartItems()")
send_msg(540, 320, 270, "List<CartItem>", dotted=True)

draw.line([320, 310, 350, 310], fill=line_c, width=1)
draw.line([350, 310, 350, 340], fill=line_c, width=1)
draw.line([350, 340, 320, 340], fill=line_c, width=1)
draw.polygon([(320, 340), (328, 336), (328, 344)], fill=line_c)
draw.text((360, 318), "computeBill(5% GST, waiver)", font=f_msg, fill=text_c)

send_msg(320, 760, 390, "placeOrder(payload)")
send_msg(760, 980, 450, "POST /api/orders (JSON)")
send_msg(980, 760, 510, "201 Created {orderId: FH-78921}", dotted=True)
send_msg(760, 320, 570, "onOrderSuccess(orderId)", dotted=True)
send_msg(320, 540, 620, "clearCart()")

draw.line([320, 680, 100, 680], fill=line_c, width=1)
draw.polygon([(100, 680), (108, 676), (108, 684)], fill=line_c)
draw.text((120, 663), "Open Live Delivery Radar Screen", font=f_msg, fill=text_c)

inv = ImageOps.invert(img.convert('L'))
bbox = inv.getbbox()
pad = 25
crop_box = (max(0, bbox[0] - pad), max(0, bbox[1] - pad), min(w, bbox[2] + pad), min(h, bbox[3] + pad))
cropped = img.crop(crop_box)
cropped.save(r'C:\FoodieHub\docs\diagrams\07_sequence_diagram_order.png', dpi=(300, 300))
cropped.save(r'C:\FoodieHub\docs\diagrams\figure_2_7_academic.png', dpi=(300, 300))
print("Sequence Diagram OK:", cropped.size)

# ==============================================================
# 2. COMPONENT DIAGRAM (4 Clean Horizontal Layers Matching Page 17)
# ==============================================================
cw = 1000
ch = 680
cimg = Image.new('RGB', (cw, ch), (255, 255, 255))
cdraw = ImageDraw.Draw(cimg)

def draw_layer_container(y, h, title, sub_boxes):
    x = 60
    w = 880
    cdraw.rectangle([x, y, x + w, y + h], fill=(250, 250, 250), outline=(130, 130, 130), width=1)
    # Title
    cdraw.text((x + 14, y + 10), title, font=f_hdr, fill=(40, 40, 40))
    # Sub boxes
    sb_w = (w - 28 - (len(sub_boxes) - 1) * 16) // len(sub_boxes)
    for i, sb_name in enumerate(sub_boxes):
        sb_x = x + 14 + i * (sb_w + 16)
        sb_y = y + 42
        sb_h = h - 56
        cdraw.rectangle([sb_x, sb_y, sb_x + sb_w, sb_y + sb_h], fill=(238, 238, 238), outline=(100, 100, 100), width=1)
        tb = cdraw.textbbox((0, 0), sb_name, font=f_box)
        tw = tb[2] - tb[0]
        th = tb[3] - tb[1]
        cdraw.text((sb_x + (sb_w - tw)//2, sb_y + (sb_h - th)//2), sb_name, font=f_box, fill=(0, 0, 0))

# 1. Presentation Layer
draw_layer_container(40, 95, "Presentation Layer (Activities + XML)", ["Login / Register", "Catalog & Explorer", "Cart & Checkout", "Delivery Radar Tracking"])

# Arrow 1 down
cdraw.line([500, 135, 500, 175], fill=line_c, width=2)
cdraw.polygon([(500, 175), (496, 167), (504, 167)], fill=line_c)

# 2. Utility Layer
draw_layer_container(175, 95, "Utility Layer", ["SessionManager", "CartAdapter", "FoodAdapter", "OrderAdapter"])

# Arrow 2 down
cdraw.line([500, 270, 500, 310], fill=line_c, width=2)
cdraw.polygon([(500, 310), (496, 302), (504, 302)], fill=line_c)

# 3. Data Layer
draw_layer_container(310, 95, "Data Layer", ["DatabaseHelper (SQLite Local)", "ApiClient (Retrofit HTTP Client)"])

# Arrow 3 down with label
cdraw.line([500, 405, 500, 465], fill=line_c, width=2)
cdraw.polygon([(500, 465), (496, 457), (504, 457)], fill=line_c)
cdraw.text((515, 428), "HTTP + JSON (port 5000)", font=f_msg, fill=(0, 0, 0))

# 4. Backend Cloud Layer
draw_layer_container(465, 95, "Backend (Node.js & MongoDB Server)", ["Express.js REST Server (server.js)", "MongoDB Atlas Cloud Database"])

cinv = ImageOps.invert(cimg.convert('L'))
cbbox = cinv.getbbox()
cpad = 25
ccrop_box = (max(0, cbbox[0] - cpad), max(0, cbbox[1] - cpad), min(cw, cbbox[2] + cpad), min(ch, cbbox[3] + cpad))
ccropped = cimg.crop(ccrop_box)
ccropped.save(r'C:\FoodieHub\docs\diagrams\08_component_diagram.png', dpi=(300, 300))
ccropped.save(r'C:\FoodieHub\docs\diagrams\figure_2_8_academic.png', dpi=(300, 300))
print("Component Diagram OK:", ccropped.size)
