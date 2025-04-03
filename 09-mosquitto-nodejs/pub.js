const client = require('mqtt').connect(process.env.MQTT_HOST || 'mqtt://localhost:1883');
client.on('connect', () => {
    setInterval(() => {
        const v = Math.random();
        client.publish('test/rnd', `${v}`);
        console.log(`Send Message: ${v}`);
    }, 1000);
});