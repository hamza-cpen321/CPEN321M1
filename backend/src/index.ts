import express from 'express';
import { createServer } from "http";
import os from "os";
import { WebSocketServer, WebSocket } from "ws";

const app = express();
const server = createServer(app);
const wss = new WebSocketServer({ server });
const stream = new WebSocket('wss://8.229.22.124');

stream.on('open', () => {
    console.log('stream established');
});

wss.on('connection', (ws) => {
  console.log('Client connected');

  ws.on('message', (message) => {
    console.log(`Received message: ${message}`);
    ws.send(`Server received your message: ${message}`);
  });
  
  stream.on('message', (data) => {
    //console.log(`Received message: ${data}`);
    ws.send(data);
  });
});
app.get('/', (_req, res) => {
  res.send('Hello World!')
})

app.get('/health', (_req, res) => {
  res.json({ status: 'ok' });
});

//API to get my first/last name
app.get('/devName', (_req, res) => {
  res.send('Hamza Islam');
});

// API to get Server IP address
app.get('/serverIP', (_req, res) => {
  const interfaces = Object.values(os.networkInterfaces()).flat();
  const address = interfaces.find(
    (networkAddress) => networkAddress?.family === 'IPv4' && !networkAddress.internal
  )?.address;

  res.send(address ?? 'unavailable');
});

// API to get server local time (hh:mm:ss GMT+hh:mm)
app.get('/serverTime', (req, res) => {
  const currentDate = new Date();
  const pad = (value: number) => String(value).padStart(2, '0');
  const offsetMinutes = -currentDate.getTimezoneOffset();
  const sign = offsetMinutes >= 0 ? '+' : '-';
  const absoluteOffset = Math.abs(offsetMinutes);
  const timezone = `GMT${sign}${pad(Math.floor(absoluteOffset / 60))}:${pad(absoluteOffset % 60)}`;
  const time = `${pad(currentDate.getHours())}:${pad(currentDate.getMinutes())}:${pad(currentDate.getSeconds())}`;

  res.send(`${time} ${timezone}`);
});

server.listen(3000, () => console.log("listening on :3000"));

