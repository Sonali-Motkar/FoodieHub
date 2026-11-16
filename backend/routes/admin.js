const express = require('express');
const router = express.Router();

// GET /api/admin/metrics - High level restaurant metrics
router.get('/metrics', (req, res) => {
    return res.status(200).json({
        success: true,
        data: {
            totalOrders: 32,
            pendingOrders: 5,
            deliveredOrders: 25,
            totalRevenue: "₹ 14,890.00",
            activeDishes: 8
        }
    });
});

module.exports = router;
