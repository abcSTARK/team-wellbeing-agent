const express = require('express');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 3000;
const SPRING_API_URL = 'http://localhost:8080/api/wellbeing/mcp/status';

app.get('/mcp/status', async (req, res) => {
  try {
    const response = await axios.get(SPRING_API_URL);
    res.json(response.data);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch wellbeing status', details: error.message });
  }
});

app.get('/mcp/slack/messages', async (req, res) => {
  const channel = req.query.channel || 'general';
  try {
    const response = await axios.get(`http://localhost:8080/api/wellbeing/slack/messages?channel=${encodeURIComponent(channel)}`);
    res.json(response.data);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch Slack messages', details: error.message });
  }
});

app.get('/mcp/slack/channels', async (req, res) => {
  try {
    const response = await axios.get('http://localhost:8080/api/wellbeing/slack/channels');
    res.json(response.data);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch Slack channels', details: error.message });
  }
});

app.get('/', (req, res) => {
  res.send('MCP JS Server is running. Use /mcp/status to fetch team wellbeing status.');
});

app.listen(PORT, () => {
  console.log(`MCP JS Server listening on port ${PORT}`);
});
