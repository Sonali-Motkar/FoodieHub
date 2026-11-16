const express = require('express');
const router = express.Router();

const categories = [
    { id: "CAT-01", name: "All", icon: "ic_all" },
    { id: "CAT-02", name: "Burgers", icon: "ic_burger" },
    { id: "CAT-03", name: "Pizzas", icon: "ic_pizza" },
    { id: "CAT-04", name: "Asian", icon: "ic_noodles" },
    { id: "CAT-05", name: "Beverages", icon: "ic_coffee" },
    { id: "CAT-06", name: "Desserts", icon: "ic_dessert" }
];

const foodCatalog = [
    {
        id: "FOD-01",
        name: "Classic Paneer Tikka Burger",
        category: "Burgers",
        price: 189.00,
        description: "Grilled spiced cottage cheese patty infused with mint-coriander chutney, sliced onions, tomatoes, and crisp lettuce.",
        imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
        rating: 4.7,
        prepTime: "20 mins",
        calories: "450 kcal",
        isAvailable: true
    },
    {
        id: "FOD-02",
        name: "Farmhouse Supreme Pizza",
        category: "Pizzas",
        price: 299.00,
        description: "Fresh pan crust loaded with mozzarella cheese, diced bell peppers, sweet golden corn, black olives, and button mushrooms.",
        imageUrl: "https://images.unsplash.com/photo-1513104890138-7c749659a591",
        rating: 4.8,
        prepTime: "25 mins",
        calories: "680 kcal",
        isAvailable: true
    },
    {
        id: "FOD-03",
        name: "Chilli Garlic Hakka Noodles",
        category: "Asian",
        price: 169.00,
        description: "Wok-tossed noodles with shredded cabbage, crunchy carrots, fiery red chillies, and fragrant roasted garlic.",
        imageUrl: "https://images.unsplash.com/photo-1585032226651-759b368d7246",
        rating: 4.5,
        prepTime: "15 mins",
        calories: "380 kcal",
        isAvailable: true
    },
    {
        id: "FOD-04",
        name: "Cold Brew Frappe Coffee",
        category: "Beverages",
        price: 99.00,
        description: "Rich dark roast espresso blended with chilled milk, dark cocoa drizzle, and whipped cream topping.",
        imageUrl: "https://images.unsplash.com/photo-1517701604599-bb29b565090c",
        rating: 4.6,
        prepTime: "10 mins",
        calories: "210 kcal",
        isAvailable: true
    },
    {
        id: "FOD-05",
        name: "Sizzling Chocolate Brownie",
        category: "Desserts",
        price: 149.00,
        description: "Warm fudgy walnut brownie served on a cast-iron skillet, smothered with rich Belgian chocolate ganache.",
        imageUrl: "https://images.unsplash.com/photo-1606313564200-e75d5e30476c",
        rating: 4.9,
        prepTime: "12 mins",
        calories: "520 kcal",
        isAvailable: true
    },
    {
        id: "FOD-06",
        name: "Crispy Peri Peri French Fries",
        category: "Burgers",
        price: 119.00,
        description: "Golden crispy potato fries dusted with authentic African peri-peri spice mix, served with garlic dip.",
        imageUrl: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877",
        rating: 4.6,
        prepTime: "10 mins",
        calories: "310 kcal",
        isAvailable: true
    },
    {
        id: "FOD-07",
        name: "Margherita Basil Pizza",
        category: "Pizzas",
        price: 249.00,
        description: "Classic Neapolitan style pizza with San Marzano tomato sauce, fresh buffalo mozzarella, and aromatic basil leaves.",
        imageUrl: "https://images.unsplash.com/photo-1604382355076-af4b0eb60143",
        rating: 4.7,
        prepTime: "20 mins",
        calories: "550 kcal",
        isAvailable: true
    },
    {
        id: "FOD-08",
        name: "Veg Manchurian Gravy Bowl",
        category: "Asian",
        price: 179.00,
        description: "Deep-fried vegetable dumplings simmered in a spicy, tangy Indo-Chinese soy and coriander gravy.",
        imageUrl: "https://images.unsplash.com/photo-1541832676-9b763b0239ab",
        rating: 4.4,
        prepTime: "18 mins",
        calories: "410 kcal",
        isAvailable: true
    }
];

// GET /api/foods/categories - List all categories
router.get('/categories', (req, res) => {
    return res.status(200).json({ success: true, count: categories.length, data: categories });
});

// GET /api/foods - List dishes (supports ?category=Burgers and ?search=Paneer)
router.get('/', (req, res) => {
    let result = [...foodCatalog];
    const { category, search } = req.query;

    if (category && category.toLowerCase() !== 'all') {
        result = result.filter(f => f.category.toLowerCase() === category.toLowerCase());
    }

    if (search && search.trim() !== '') {
        const q = search.toLowerCase();
        result = result.filter(f => f.name.toLowerCase().includes(q) || f.description.toLowerCase().includes(q));
    }

    return res.status(200).json({ success: true, count: result.length, data: result });
});

// GET /api/foods/:id - Single food item details
router.get('/:id', (req, res) => {
    const food = foodCatalog.find(f => f.id === req.params.id);
    if (!food) {
        return res.status(404).json({ success: false, message: 'Food item not found' });
    }
    return res.status(200).json({ success: true, data: food });
});

module.exports = router;
