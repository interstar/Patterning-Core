// Helper function to transform coordinates from viewport to window space
function tx(vx1, vx2, wx1, wx2, x) {
    return ((x - vx1) / (vx2 - vx1)) * (wx2 - wx1) + wx1;
}

// Convert Patterning color [r,g,b,a] to CSS color string
function patterningColorToCSS(color) {
    if (!color) return 'black';
    if (typeof color === 'string') return color;
    
    const [r, g, b, a = 255] = color;
    if (a === 255) {
        return `rgb(${Math.round(r)}, ${Math.round(g)}, ${Math.round(b)})`;
    } else {
        return `rgba(${Math.round(r)}, ${Math.round(g)}, ${Math.round(b)}, ${a/255})`;
    }
}

// Main rendering function
function renderToCanvas(pattern, canvas, viewport = [-1, -1, 1, 1], window = [0, 0, canvas.width, canvas.height]) {
    const ctx = canvas.getContext('2d');
    
    // Transform point from viewport to window coordinates
    const txpt = (x, y) => {
        const [vx1, vy1, vx2, vy2] = viewport;
        const [wx1, wy1, wx2, wy2] = window;
        return {
            x: tx(vx1, vx2, wx1, wx2, x),
            y: tx(vy1, vy2, wy1, wy2, y)
        };
    };

    // Clear the canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Render each SShape
    pattern.forEach(sshape => {
        const {style, points} = sshape;
        
        // Set style properties
        ctx.strokeStyle = patterningColorToCSS(style.stroke);
        ctx.fillStyle = patterningColorToCSS(style.fill);
        ctx.lineWidth = style['stroke-weight'] || 1;
        
        // Draw the shape
        ctx.beginPath();
        const firstPoint = txpt(points[0][0], points[0][1]);
        ctx.moveTo(firstPoint.x, firstPoint.y);
        
        if (style.bezier) {
            // Handle bezier curves
            for (let i = 1; i < points.length; i += 3) {
                const p1 = txpt(points[i][0], points[i][1]);
                const p2 = txpt(points[i+1][0], points[i+1][1]);
                const p3 = txpt(points[i+2][0], points[i+2][1]);
                ctx.bezierCurveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
            }
        } else {
            // Handle regular lines
            for (let i = 1; i < points.length; i++) {
                const point = txpt(points[i][0], points[i][1]);
                ctx.lineTo(point.x, point.y);
            }
        }
        
        if (style.fill && style.fill !== 'none') {
            ctx.fill();
        }
        ctx.stroke();
    });
}

// Function to handle responsive canvas sizing
function setupResponsiveCanvas(canvas, pattern) {
    function resizeCanvas() {
        const container = canvas.parentElement;
        const containerWidth = container.clientWidth;
        const containerHeight = container.clientHeight;
        
        // Set canvas size to match container
        canvas.width = containerWidth;
        canvas.height = containerHeight;
        
        // Render the pattern
        renderToCanvas(pattern, canvas);
    }

    // Initial setup
    resizeCanvas();
    
    // Handle window resize
    window.addEventListener('resize', resizeCanvas);
} 