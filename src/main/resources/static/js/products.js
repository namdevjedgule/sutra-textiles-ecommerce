(function () {
    window.Sutra = window.Sutra || {};

    const CATEGORY_LABELS = { sarees: 'Sarees', lehengas: 'Lehengas', 'salwar-suits': 'Salwar Suits', regional: 'Regional Wear' };
    const REGION_LABELS = { rajasthan: 'Rajasthan', bengal: 'Bengal', 'south-india': 'South India', punjab: 'Punjab', gujarat: 'Gujarat', kashmir: 'Kashmir', maharashtra: 'Maharashtra' };
    const SKU_PREFIX = { sarees: 'SR', lehengas: 'LH', 'salwar-suits': 'SS', regional: 'RW' };

    const SIZES_BY_CATEGORY = {
        sarees: ['Free Size'],
        lehengas: ['XS', 'S', 'M', 'L', 'XL', 'XXL'],
        'salwar-suits': ['S', 'M', 'L', 'XL', 'XXL'],
        regional: ['S', 'M', 'L', 'XL']
    };

    const INCLUDES_BY_CATEGORY = {
        sarees: 'Saree (5.5 m) with unstitched blouse piece (0.8 m)',
        lehengas: 'Lehenga, blouse and dupatta',
        'salwar-suits': 'Kurta, bottom and dupatta',
        regional: 'Top and dupatta'
    };

    const BASE = [
        [1, 'Bandhani Silk Saree', 'sarees', 'rajasthan', 3450, 'Bestseller', 1, 'Gaji Silk', 'Bandhani tie-dye', 'Festive, Wedding Guest',
            [['Madder Red', '#A63A2B'], ['Emerald', '#1F5A44'], ['Mustard', '#C9A24B']],
            'A saree inspired by the tie-dye dots of Rajasthan\u2019s bandhani tradition, in a fluid silk that drapes beautifully and photographs even better.'],
        [2, 'Kanjeevaram Silk Saree', 'sarees', 'south-india', 8990, 'New', 2, 'Kanjeevaram-style Silk', 'Zari border and pallu', 'Wedding, Festive',
            [['Temple Maroon', '#7A1F2B'], ['Peacock', '#1F5C63'], ['Royal Purple', '#4B2E6F']],
            'Rich temple-border zari and a weighty silk body \u2014 inspired by the South Indian wedding silks that are passed down for generations.'],
        [3, 'Tant Cotton Saree', 'sarees', 'bengal', 2100, null, 3, 'Soft Tant Cotton', 'Woven border', 'Everyday, Office, Puja',
            [['Ivory & Red', '#B5533C'], ['Indigo', '#2E3F6B'], ['Sage', '#7F8F6A']],
            'Light, breathable and crisp \u2014 a Bengal-inspired tant saree made for long days, with a border that does all the talking.'],
        [4, 'Paithani Silk Saree', 'sarees', 'maharashtra', 6750, null, 4, 'Paithani-style Silk', 'Peacock motif pallu', 'Wedding, Festive',
            [['Rani Pink', '#B03A6B'], ['Teal', '#1F6F6B'], ['Antique Gold', '#B68A2E']],
            'A peacock-motif pallu and a gleaming silk body, inspired by Maharashtra\u2019s Paithani heritage.'],
        [5, 'Mirrorwork Bridal Lehenga', 'lehengas', 'rajasthan', 12500, 'Bestseller', 2, 'Raw Silk and Net', 'Mirrorwork and zardozi', 'Bridal, Sangeet',
            [['Crimson', '#8E1F2F'], ['Bottle Green', '#1F4B3A'], ['Ivory Gold', '#E8DCC0']],
            'A statement bridal lehenga with dense mirrorwork and zardozi \u2014 a full, graceful flare made to move through ceremonies.'],
        [6, 'Patola Silk Lehenga', 'lehengas', 'gujarat', 9800, null, 1, 'Patola-style Silk', 'Double-ikat inspired weave', 'Wedding, Reception',
            [['Indigo Red', '#7B2638'], ['Parrot Green', '#2F6B3F'], ['Midnight', '#22304F']],
            'Geometric double-ikat inspired patterning on a lustrous silk \u2014 Gujarat\u2019s patola spirit in a lehenga silhouette.'],
        [7, 'Organza Festive Lehenga', 'lehengas', 'punjab', 7200, 'New', 3, 'Organza', 'Thread and sequin embroidery', 'Festive, Engagement',
            [['Blush', '#D9A5A0'], ['Sky Blue', '#7FA6C4'], ['Lilac', '#A58BB8']],
            'A light, airy organza lehenga with delicate thread and sequin work \u2014 festive without the weight.'],
        [8, 'Kalamkari Print Lehenga', 'lehengas', 'south-india', 5600, null, 4, 'Cotton Silk', 'Kalamkari-style block print', 'Festive, Day Events',
            [['Rust', '#A4502B'], ['Deep Teal', '#24585C'], ['Ochre', '#BE8A2E']],
            'Hand-block inspired kalamkari prints on a comfortable cotton silk \u2014 easy to wear, easy to love.'],
        [9, 'Phulkari Palazzo Suit', 'salwar-suits', 'punjab', 2650, null, 1, 'Cotton', 'Phulkari embroidery', 'Festive, Casual',
            [['Marigold', '#D79A1E'], ['Magenta', '#A3246B'], ['Leaf Green', '#4B7A3B']],
            'Bright phulkari-style embroidery on soft cotton, paired with easy palazzos \u2014 Punjab\u2019s cheer in everyday comfort.'],
        [10, 'Kashmiri Embroidered Suit', 'salwar-suits', 'kashmir', 4300, 'New', 2, 'Pashmina Blend', 'Needle embroidery', 'Festive, Winter Occasions',
            [['Charcoal', '#3A3F45'], ['Wine', '#6B2438'], ['Pine', '#2E4A3C']],
            'Fine needle-embroidered florals on a warm pashmina blend \u2014 inspired by the valley\u2019s most delicate craft.'],
        [11, 'Ajrakh Print Suit', 'salwar-suits', 'gujarat', 2950, null, 3, 'Modal Cotton', 'Ajrakh-style block print', 'Everyday, Office',
            [['Indigo', '#2C3A6B'], ['Madder', '#9B3B2C'], ['Black & Ivory', '#2A2A28']],
            'Deep ajrakh-inspired prints in an earthy palette on a cool, soft modal cotton.'],
        [12, 'Bandhani Palazzo Suit', 'salwar-suits', 'rajasthan', 3100, null, 4, 'Cotton Silk', 'Bandhani print', 'Festive, Day Events',
            [['Pomegranate', '#A32F45'], ['Turmeric', '#D3A12A'], ['Bottle Green', '#245238']],
            'Playful bandhani dots on a breezy cotton silk, with wide-leg palazzos for effortless movement.'],
        [13, 'Phulkari Cotton Kurti', 'regional', 'punjab', 1650, null, 1, 'Cotton', 'Phulkari embroidery', 'Everyday, Casual',
            [['Mustard', '#C9A24B'], ['Teal', '#2F6B6B'], ['Rose', '#C26B7A']],
            'A relaxed everyday kurti with phulkari-style embroidery at the yoke.', 'Kurti'],
        [14, 'Jamdani Dupatta Set', 'regional', 'bengal', 1950, null, 3, 'Cotton Blend', 'Jamdani-style woven motifs', 'Everyday, Puja',
            [['White & Red', '#B5533C'], ['Ink Blue', '#27386A'], ['Forest', '#2D5240']],
            'A kurta set with a jamdani-inspired woven dupatta \u2014 soft, light and quietly elegant.'],
        [15, 'Kasavu Festive Set', 'regional', 'south-india', 3800, null, 2, 'Cotton Silk', 'Kasavu zari border', 'Onam, Festive',
            [['Cream & Gold', '#E8DCC0'], ['Pista', '#A9B98A'], ['Rose Gold', '#C9957A']],
            'Cream and gold in the Kerala kasavu tradition \u2014 a festive set that feels crisp and celebratory.'],
        [16, 'Kashmiri Pashmina Shawl Set', 'regional', 'kashmir', 5400, 'Bestseller', 4, 'Pashmina Blend', 'Kani-inspired weave', 'Winter, Gifting',
            [['Camel', '#B08A5E'], ['Slate', '#4E5963'], ['Berry', '#6E2D4A']],
            'A soft pashmina-blend shawl set with kani-inspired weaving \u2014 warm, light and gift-worthy.', 'Shawl and stole']
    ];

    function careFor(fabric) {
        if (/silk|organza|net|pashmina/i.test(fabric)) {
            return ['Dry clean only', 'Store folded in a muslin cloth', 'Avoid direct sunlight and perfumes on the fabric', 'Steam or iron on low heat on the reverse'];
        }
        return ['Gentle hand wash in cold water', 'Wash separately for the first two washes', 'Dry in shade', 'Iron on medium heat on the reverse'];
    }

    function buildSizes(id, category) {
        const labels = SIZES_BY_CATEGORY[category];
        return labels.map((label, i) => {
            let qty = labels.length === 1 ? 14 : (id * 5 + i * 3) % 10;
            if (labels.length > 1 && i === 1 && qty === 0) qty = 6;
            return { label, qty };
        });
    }

    const all = BASE.map(row => {
        const [id, name, category, region, price, tag, tint, fabric, work, occasion, colorRows, blurb, includesOverride] = row;
        const madeToOrder = id % 5 === 0;
        return {
            id, name, category, region, price, tag, tint, fabric, work, occasion,
            mrp: Math.round(price * 1.18 / 50) * 50,
            colors: colorRows.map(c => ({ name: c[0], hex: c[1] })),
            sizes: buildSizes(id, category),
            stock: madeToOrder ? 'made-to-order' : 'in-stock',
            dispatchDays: madeToOrder ? [7, 10] : [1, 2],
            sku: `STI-${SKU_PREFIX[category]}-${String(id).padStart(4, '0')}`,
            description: blurb,
            highlights: [
                `${fabric} with ${work.toLowerCase()}`,
                'Made in-house at our Bhilwara, Rajasthan facility',
                `Suited for ${occasion.toLowerCase()}`
            ],
            includes: includesOverride || INCLUDES_BY_CATEGORY[category],
            care: careFor(fabric),
            images: null
        };
    });

    const catalog = {
        all,
        categoryLabels: CATEGORY_LABELS,
        regionLabels: REGION_LABELS,
        byId(id) { return all.find(p => p.id === Number(id)) || null; },
        categoryLabel(key) { return CATEGORY_LABELS[key] || key; },
        regionLabel(key) { return REGION_LABELS[key] || key; },
        money(n) { return '\u20B9' + Number(n).toLocaleString('en-IN'); },
        discount(p) { return p.mrp > p.price ? Math.round((1 - p.price / p.mrp) * 100) : 0; },
        needsSize(p) { return p.sizes.length > 1; },
        firstAvailableSize(p) { const s = p.sizes.find(s => s.qty > 0); return s ? s.label : null; },
        defaultColor(p) { return p.colors[0].name; },
        related(p, n = 4) {
            const others = all.filter(o => o.id !== p.id);
            const score = o => (o.category === p.category ? 2 : 0) + (o.region === p.region ? 1 : 0);
            return others.sort((a, b) => score(b) - score(a) || a.id - b.id).slice(0, n);
        }
    };

    window.Sutra.catalog = catalog;
})();