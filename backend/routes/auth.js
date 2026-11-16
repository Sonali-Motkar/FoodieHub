const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Sample in-memory user registry for standalone execution without DB overhead
const usersDB = [
    {
        id: "USR-101",
        name: "Sonali Motkar",
        email: "sonali.m@gmail.com",
        passwordHash: "$2a$10$wN9iZ8J...demo", // Pass@123
        phone: "+91 9876543210",
        address: "Flat 402, Green Palms, Kothrud, Pune - 411038",
        role: "customer"
    },
    {
        id: "ADM-001",
        name: "Master Admin",
        email: "admin@foodiehub.com",
        passwordHash: "$2a$10$wN9iZ8J...demo", // Admin@2026
        phone: "+91 9822000000",
        address: "FoodieHub Central Kitchen, FC Road, Pune",
        role: "admin"
    }
];

const JWT_SECRET = process.env.JWT_SECRET || 'foodiehub_mca_super_secret_jwt_key_2026';

// POST /api/auth/register
router.post('/register', async (req, res) => {
    try {
        const { name, email, password, phone, deliveryAddress } = req.body;

        if (!name || !email || !password) {
            return res.status(400).json({ success: false, message: 'Name, email, and password are required' });
        }

        const existingUser = usersDB.find(u => u.email.toLowerCase() === email.toLowerCase());
        if (existingUser) {
            return res.status(400).json({ success: false, message: 'An account with this email already exists' });
        }

        const salt = await bcrypt.genSalt(10);
        const passwordHash = await bcrypt.hash(password, salt);

        const newUser = {
            id: `USR-${100 + usersDB.length + 1}`,
            name,
            email,
            passwordHash,
            phone: phone || '',
            address: deliveryAddress || '',
            role: 'customer'
        };

        usersDB.push(newUser);

        const token = jwt.sign({ id: newUser.id, email: newUser.email, role: newUser.role }, JWT_SECRET, { expiresIn: '14d' });

        return res.status(201).json({
            success: true,
            message: 'User registered successfully',
            token,
            user: {
                id: newUser.id,
                name: newUser.name,
                email: newUser.email,
                phone: newUser.phone,
                address: newUser.address,
                role: newUser.role
            }
        });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ success: false, message: 'Please provide email and password' });
        }

        // Support admin default login
        if (email.toLowerCase() === 'admin@foodiehub.com' && password === 'Admin@2026') {
            const token = jwt.sign({ id: 'ADM-001', email, role: 'admin' }, JWT_SECRET, { expiresIn: '7d' });
            return res.status(200).json({
                success: true,
                message: 'Admin authentication successful',
                token,
                user: { id: 'ADM-001', name: 'Master Admin', email, role: 'admin', phone: '+91 9822000000', address: 'Central Kitchen, Pune' }
            });
        }

        const user = usersDB.find(u => u.email.toLowerCase() === email.toLowerCase());
        if (!user) {
            // For testing ease, auto-create student account if it's Sonali
            if (email.toLowerCase() === 'sonali.m@gmail.com') {
                const token = jwt.sign({ id: 'USR-101', email, role: 'customer' }, JWT_SECRET, { expiresIn: '14d' });
                return res.status(200).json({
                    success: true,
                    message: 'Login successful',
                    token,
                    user: { id: 'USR-101', name: 'Sonali Motkar', email, role: 'customer', phone: '+91 9876543210', address: 'Kothrud, Pune' }
                });
            }
            return res.status(401).json({ success: false, message: 'Invalid email or password' });
        }

        const token = jwt.sign({ id: user.id, email: user.email, role: user.role }, JWT_SECRET, { expiresIn: '14d' });
        return res.status(200).json({
            success: true,
            message: 'Login successful',
            token,
            user: { id: user.id, name: user.name, email: user.email, phone: user.phone, address: user.address, role: user.role }
        });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

module.exports = router;
