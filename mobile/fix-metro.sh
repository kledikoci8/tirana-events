#!/bin/bash

# Metro Bundler Troubleshooting Script
# Run this if Metro keeps disconnecting

echo "🔧 Metro Bundler Troubleshooting Script"
echo "========================================"
echo ""

# Check if port 8081 is in use
echo "1️⃣  Checking if port 8081 is in use..."
PORT_CHECK=$(lsof -i :8081 | grep LISTEN)
if [ -n "$PORT_CHECK" ]; then
    echo "⚠️  Port 8081 is in use:"
    echo "$PORT_CHECK"
    echo ""
    read -p "Kill the process? (y/n): " KILL_PROCESS
    if [ "$KILL_PROCESS" = "y" ]; then
        PID=$(echo "$PORT_CHECK" | awk '{print $2}')
        kill -9 $PID
        echo "✅ Process killed"
    fi
else
    echo "✅ Port 8081 is available"
fi
echo ""

# Get Mac's local IP
echo "2️⃣  Your Mac's local IP addresses:"
ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print "   " $2}'
echo ""
echo "📝 Update DEV_MACHINE_IP in mobile/src/config/apiConfig.js if using physical device"
echo ""

# Check if node_modules exists
echo "3️⃣  Checking node_modules..."
if [ -d "node_modules" ]; then
    echo "✅ node_modules exists"
else
    echo "⚠️  node_modules not found"
    read -p "Run npm install? (y/n): " RUN_INSTALL
    if [ "$RUN_INSTALL" = "y" ]; then
        npm install
    fi
fi
echo ""

# Clear Metro cache
echo "4️⃣  Clearing Metro bundler cache..."
rm -rf node_modules/.cache
rm -rf .expo
echo "✅ Cache cleared"
echo ""

# Clear watchman cache (if installed)
if command -v watchman &> /dev/null; then
    echo "5️⃣  Clearing Watchman cache..."
    watchman watch-del-all
    echo "✅ Watchman cache cleared"
else
    echo "5️⃣  Watchman not installed (optional)"
fi
echo ""

# Start Metro with reset cache
echo "6️⃣  Starting Metro bundler with clean cache..."
echo ""
echo "Choose an option:"
echo "  1) Start normally (npm start)"
echo "  2) Start with reset cache (npm start -- --reset-cache)"
echo "  3) Start with tunnel (expo start --tunnel)"
echo "  4) Exit"
echo ""
read -p "Enter choice (1-4): " START_CHOICE

case $START_CHOICE in
    1)
        npm start
        ;;
    2)
        npm start -- --reset-cache
        ;;
    3)
        expo start --tunnel
        ;;
    4)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "Invalid choice"
        exit 1
        ;;
esac
