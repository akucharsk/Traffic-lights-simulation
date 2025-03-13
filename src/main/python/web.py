import fastapi
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from starlette.requests import Request

import os
import socket
import json
import signal

MAX_MSG_LENGTH = 4096

app = fastapi.FastAPI()
static_dir = os.path.join("src", "main", "resources", "static")
app.mount("/static", StaticFiles(directory=static_dir), name="static")

templates_dir = os.path.join("src", "main", "resources", "templates")
templates = Jinja2Templates(directory=templates_dir)

conn = None
run = True
java_address = "127.0.0.1", 10000
try:
    conn = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    conn.bind(("127.0.0.1", 10002))
    conn.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    print("Java socket accepted")
    conn.sendto(bytes("abhasdfosdnjsdf", encoding="utf-8"), java_address)
    msg, addr = conn.recvfrom(MAX_MSG_LENGTH)
    print("Message sent")

except Exception as e:
    print(e.with_traceback(None))


def handle_signal(signum, extra):
    if conn is not None:
        conn.sendto(bytes("STOP", "utf-8"), java_address)
        conn.close()


signal.signal(signal.SIGINT, handle_signal)


async def send(msg, attempts=0):
    if attempts == 5:
        raise Exception("Unable to send message")
    conn.settimeout(5.0)
    try:
        conn.sendto(bytes(msg, encoding="utf-8"), java_address)
        return conn.recvfrom(MAX_MSG_LENGTH)[0]
    except socket.timeout as e:
        return await send(msg, attempts + 1)


@app.get("/", response_class=fastapi.responses.HTMLResponse)
async def hello(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})


@app.post("/api/vehicles")
async def add_vehicle(request: Request):
    data = await request.json()
    msg = await send(f"VADD;{data['vehicleID']};{data['startRoad']};{data['endRoad']};")
    return fastapi.responses.JSONResponse(content=str(msg, 'utf-8'), status_code=200)
