const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const connectDB = require('./config/db');

dotenv.config();
connectDB();

const app = express();

app.use(cors());
app.use(express.json());

// Log incoming API calls
app.use((req, res, next) => {
    console.log(`[${new Date().toLocaleTimeString()}] ${req.method} ${req.url}`);
    next();
});

// Mount Routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/foods', require('./routes/foods'));
app.use('/api/orders', require('./routes/orders'));
app.use('/api/admin', require('./routes/admin'));

// Root & Health
app.get('/', (req, res) => {
    res.json({
        app: 'FoodieHub REST API Server',
        status: 'Online',
        version: '1.0.0',
        author: 'Sonali Motkar (MCA Sem-III)',
        endpoints: [
            '/api/auth/login',
            '/api/auth/register',
            '/api/foods',
            '/api/foods/categories',
            '/api/orders',
            '/api/admin/metrics'
        ]
    });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`====================================================`);
    console.log(`🚀 FoodieHub Server running on http://localhost:${PORT}`);
    console.log(`📱 Connect your Android app using your PC's Wi-Fi IP`);
    console.log(`====================================================`);
});
