import { env } from './config/env';
import express from 'express';

const app = express()

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
  const ip = _req.ip;
  res.send(ip)
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

app.use((_req, res) => {
  res.status(404).json({ error: 'Not Found' });
});

const server = app.listen(env.port, () => {
  console.log(`Server listening on port ${env.port}`);
});

for (const signal of ['SIGINT', 'SIGTERM'] as const) {
  process.on(signal, () => {
    server.close(() => {
      process.exit(0);
    });
  });
}


