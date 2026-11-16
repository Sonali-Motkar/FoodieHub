const mongoose = require('mongoose');

const connectDB = async () => {
    try {
        const uri = process.env.MONGO_URI;
        if (!uri) {
            console.warn('⚠️ MONGO_URI not defined in .env. Running in offline/in-memory mode for development.');
            return;
        }

        const conn = await mongoose.connect(uri, {
            serverSelectionTimeoutMS: 5000 // Fast fail if cloud is unreachable
        });

        console.log(`✅ MongoDB Atlas Connected: ${conn.connection.host}`);
    } catch (error) {
        console.warn(`⚠️ Cloud MongoDB connection notice: ${error.message}`);
        console.log('ℹ️ Server will continue operating with in-memory sample records for seamless local testing.');
    }
};

module.exports = connectDB;
