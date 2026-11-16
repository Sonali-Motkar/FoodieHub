const express = require('express');
const router = express.Router();

// In-memory orders state for persistent order handling
const orders = [
    {
        orderId: "ORD-2026-001",
        userId: "USR-101",
        customerName: "Sonali Motkar",
        items: [
            { foodId: "FOD-01", foodName: "Classic Paneer Tikka Burger", quantity: 2, priceAtPurchase: 189.00, subtotal: 378.00 },
            { foodId: "FOD-04", foodName: "Cold Brew Frappe Coffee", quantity: 1, priceAtPurchase: 99.00, subtotal: 99.00 }
        ],
        subtotal: 477.00,
        taxAmount: 23.85,
        deliveryFee: 0.00, // Free over 399
        totalAmount: 500.85,
        deliveryAddress: "Flat 402, Green Palms, Kothrud, Pune - 411038",
        orderStatus: "Confirmed",
        paymentMode: "UPI",
        createdAt: "2026-11-16T11:32:00Z"
    },
    {
        orderId: "ORD-2026-002",
        userId: "USR-102",
        customerName: "Rahul Sharma",
        items: [
            { foodId: "FOD-02", foodName: "Farmhouse Supreme Pizza", quantity: 1, priceAtPurchase: 299.00, subtotal: 299.00 },
            { foodId: "FOD-03", foodName: "Chilli Garlic Hakka Noodles", quantity: 1, priceAtPurchase: 169.00, subtotal: 169.00 }
        ],
        subtotal: 468.00,
        taxAmount: 23.40,
        deliveryFee: 0.00,
        totalAmount: 491.40,
        deliveryAddress: "Tower B, Amanora Park Town, Hadapsar, Pune",
        orderStatus: "Preparing",
        paymentMode: "Cash on Delivery",
        createdAt: "2026-11-16T11:45:00Z"
    }
];

// POST /api/orders - Submit food order
router.post('/', (req, res) => {
    try {
        const { userId, customerName, items, deliveryAddress, paymentMode } = req.body;

        if (!items || !items.length) {
            return res.status(400).json({ success: false, message: 'Cart items cannot be empty' });
        }
        if (!deliveryAddress || !deliveryAddress.trim()) {
            return res.status(400).json({ success: false, message: 'Delivery address is required' });
        }

        // Calculate item subtotals PRESERVING purchase-time price
        let subtotal = 0;
        const preservedItems = items.map(item => {
            const unitPrice = parseFloat(item.unitPrice || item.price || 0);
            const qty = parseInt(item.quantity || 1, 10);
            const lineTotal = +(unitPrice * qty).toFixed(2);
            subtotal += lineTotal;

            return {
                foodId: item.foodId || item.id,
                foodName: item.foodName || item.name,
                quantity: qty,
                priceAtPurchase: unitPrice,
                subtotal: lineTotal
            };
        });

        const taxAmount = +(subtotal * 0.05).toFixed(2);
        const deliveryFee = subtotal >= 399.0 ? 0.00 : 40.00;
        const totalAmount = +(subtotal + taxAmount + deliveryFee).toFixed(2);

        const newId = `ORD-2026-${String(orders.length + 1).padStart(3, '0')}`;
        const newOrder = {
            orderId: newId,
            userId: userId || "USR-101",
            customerName: customerName || "Sonali Motkar",
            items: preservedItems,
            subtotal: +subtotal.toFixed(2),
            taxAmount,
            deliveryFee,
            totalAmount,
            deliveryAddress: deliveryAddress.trim(),
            orderStatus: "Placed",
            paymentMode: paymentMode || "Cash on Delivery",
            createdAt: new Date().toISOString()
        };

        orders.unshift(newOrder); // Prepend to orders list

        return res.status(201).json({
            success: true,
            message: 'Order placed successfully',
            orderId: newId,
            totalAmount: totalAmount,
            orderStatus: "Placed",
            order: newOrder
        });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

// GET /api/orders/user/:userId - Customer order history
router.get('/user/:userId', (req, res) => {
    const userOrders = orders.filter(o => o.userId === req.params.userId || req.params.userId === 'all');
    return res.status(200).json({ success: true, count: userOrders.length, data: userOrders });
});

// GET /api/orders - All orders (for admin/monitoring)
router.get('/', (req, res) => {
    return res.status(200).json({ success: true, count: orders.length, data: orders });
});

// PATCH /api/orders/:orderId/status - Update order lifecycle status
router.patch('/:orderId/status', (req, res) => {
    const { status } = req.body;
    const allowed = ['Placed', 'Confirmed', 'Preparing', 'Out for Delivery', 'Delivered', 'Cancelled'];

    if (!allowed.includes(status)) {
        return res.status(400).json({
            success: false,
            message: `Invalid status. Must be one of: ${allowed.join(', ')}`
        });
    }

    const order = orders.find(o => o.orderId === req.params.orderId);
    if (!order) {
        return res.status(404).json({ success: false, message: 'Order not found' });
    }

    order.orderStatus = status;
    return res.status(200).json({
        success: true,
        message: `Order status updated to ${status}`,
        order
    });
});

module.exports = router;
