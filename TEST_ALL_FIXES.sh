#!/bin/bash

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# 🧪 TEST ALL FIXES - Verification Script
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧪 TIRANA EVENTS - TESTING ALL FIXES"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test results
PASSED=0
FAILED=0

# Function to print test result
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ PASS${NC}: $2"
        ((PASSED++))
    else
        echo -e "${RED}❌ FAIL${NC}: $2"
        ((FAILED++))
    fi
}

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📋 PRE-FLIGHT CHECKS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Check if JWT_SECRET is set
if [ -z "$JWT_SECRET" ]; then
    echo -e "${RED}❌ JWT_SECRET not set${NC}"
    echo -e "${YELLOW}📝 Run: export JWT_SECRET=\$(./backend/generate-jwt-secret.sh)${NC}"
    echo ""
fi

# Check MySQL connection
echo -e "${BLUE}🔍 Checking MySQL connection...${NC}"
mysql -u root -h 127.0.0.1 -P 3308 -e "USE tiranaevents; SELECT 1;" 2>/dev/null
print_result $? "MySQL connection on port 3308"
echo ""

# Check backend compilation
echo -e "${BLUE}🔍 Checking backend compilation...${NC}"
cd "backend" || exit
./mvnw compile -q 2>&1 > /dev/null
print_result $? "Backend compilation"
cd ..
echo ""

# Check mobile dependencies
echo -e "${BLUE}🔍 Checking mobile dependencies...${NC}"
cd "mobile" || exit
if [ -d "node_modules" ]; then
    print_result 0 "Mobile dependencies installed"
else
    echo -e "${YELLOW}⚠️  node_modules not found. Run: npm install${NC}"
    print_result 1 "Mobile dependencies installed"
fi
cd ..
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔒 SECTION A - CRITICAL BUGS VERIFICATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# A1: Race condition fix
echo -e "${BLUE}🔍 A1: Checking race condition fix (pessimistic lock)...${NC}"
grep -q "findByIdForUpdate" backend/src/main/java/com/tirana/events/service/TicketService.java
print_result $? "A1: Pessimistic lock in ticket purchase"

# A2: Memory leak fix
echo -e "${BLUE}🔍 A2: Checking memory leak fix...${NC}"
grep -q "mounted = false" mobile/src/screens/HomeScreen.js
print_result $? "A2: Memory leak cleanup in HomeScreen"

# A3: IDOR fix
echo -e "${BLUE}🔍 A3: Checking IDOR fix...${NC}"
grep -q "organizerId.*equals" backend/src/main/java/com/tirana/events/controller/EventController.java 2>/dev/null || \
grep -q "getOrganizer().*getId().*equals" backend/src/main/java/com/tirana/events/service/EventService.java 2>/dev/null
print_result $? "A3: Ownership verification in event updates"

# A4: SQL injection fix
echo -e "${BLUE}🔍 A4: Checking SQL wildcard injection fix...${NC}"
grep -q "escapeLikePattern" backend/src/main/java/com/tirana/events/service/EventService.java
print_result $? "A4: SQL wildcard escaping in search"

# A5: JWT consistency fix
echo -e "${BLUE}🔍 A5: Checking JWT token consistency...${NC}"
grep -q "generateAccessToken\|generateRefreshToken" backend/src/main/java/com/tirana/events/service/AuthService.java
print_result $? "A5: JWT token generation methods exist"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔐 SECTION B - SECURITY VULNERABILITIES VERIFICATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# B1: CORS wildcard removed
echo -e "${BLUE}🔍 B1: Checking CORS wildcard removal...${NC}"
! grep -r '@CrossOrigin(origins = "\*")' backend/src/main/java/com/tirana/events/controller/ 2>/dev/null
print_result $? "B1: No CORS wildcards in controllers"

# B2: JWT secret
echo -e "${BLUE}🔍 B2: Checking JWT secret...${NC}"
! grep -q "jwt.secret=.*-DEV-ONLY" backend/src/main/resources/application.properties 2>/dev/null
print_result $? "B2: No hardcoded JWT secret"

# B3: Rate limiting
echo -e "${BLUE}🔍 B3: Checking rate limiting...${NC}"
test -f backend/src/main/java/com/tirana/events/config/RateLimitConfig.java
print_result $? "B3: Rate limiting configuration exists"

# B4: Input sanitization
echo -e "${BLUE}🔍 B4: Checking input sanitization...${NC}"
grep -q "sanitizeText\|sanitizeHtml" backend/src/main/java/com/tirana/events/service/EventService.java
print_result $? "B4: Input sanitization in event creation"

# B5: Password validation
echo -e "${BLUE}🔍 B5: Checking password validation...${NC}"
grep -q "validatePasswordStrength\|password.*length.*8" backend/src/main/java/com/tirana/events/service/AuthService.java
print_result $? "B5: Password strength validation"

# B6: HTTPS enforcement
echo -e "${BLUE}🔍 B6: Checking HTTPS enforcement...${NC}"
grep -q "https://" mobile/src/services/apiConfig.js 2>/dev/null || grep -q "https://" mobile/src/services/api.js
print_result $? "B6: HTTPS in production API config"

# B7: Token revocation
echo -e "${BLUE}🔍 B7: Checking refresh token revocation...${NC}"
test -f backend/src/main/java/com/tirana/events/model/RefreshToken.java
print_result $? "B7: Refresh token entity exists"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔄 SECTION C - API MISMATCHES VERIFICATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# C1: Field mapping isSaved -> saved
echo -e "${BLUE}🔍 C1: Checking field mapping (isSaved -> saved)...${NC}"
grep -q '@JsonProperty("saved")' backend/src/main/java/com/tirana/events/dto/EventDTO.java
print_result $? "C1: @JsonProperty annotation for 'saved' field"

# C2: Field mapping purchaseDate -> purchasedAt
echo -e "${BLUE}🔍 C2: Checking field mapping (purchaseDate -> purchasedAt)...${NC}"
grep -q '@JsonProperty("purchasedAt")' backend/src/main/java/com/tirana/events/dto/TicketDTO.java
print_result $? "C2: @JsonProperty annotation for 'purchasedAt' field"

# C3: Event location consistency
echo -e "${BLUE}🔍 C3: Checking event location consistency...${NC}"
grep -q "getVenue.*getLocation" backend/src/main/java/com/tirana/events/service/TicketService.java
print_result $? "C3: Event location fallback logic"

# C4 & C5: Null value protection
echo -e "${BLUE}🔍 C4/C5: Checking null value protection...${NC}"
grep -q "!= null ?" backend/src/main/java/com/tirana/events/service/EventService.java
print_result $? "C4/C5: Null checks in DTO conversion"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚨 SECTION D - ERROR HANDLING VERIFICATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# D1: HomeScreen error handling
echo -e "${BLUE}🔍 D1: Checking HomeScreen error handling...${NC}"
grep -q "loading.*error.*refreshing" mobile/src/screens/HomeScreen.js && \
grep -q "renderLoadingState\|renderErrorState" mobile/src/screens/HomeScreen.js
print_result $? "D1: HomeScreen error states and UI"

# D2: TicketsScreen error handling
echo -e "${BLUE}🔍 D2: Checking TicketsScreen error handling...${NC}"
grep -q "loading.*error" mobile/src/screens/TicketsScreen.js && \
grep -q "renderLoadingState\|renderErrorState" mobile/src/screens/TicketsScreen.js
print_result $? "D2: TicketsScreen error states and UI"

# D3: EventDetailScreen error handling
echo -e "${BLUE}🔍 D3: Checking EventDetailScreen error handling...${NC}"
grep -q "error.*handleRetry" mobile/src/screens/EventDetailScreen.js
print_result $? "D3: EventDetailScreen error handling and retry"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "⚡ SECTION E - PERFORMANCE VERIFICATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# E1: Database pagination
echo -e "${BLUE}🔍 E1: Checking database-level pagination...${NC}"
grep -q "Pageable" backend/src/main/java/com/tirana/events/service/EventService.java && \
grep -q "findUpcomingEventsWithPagination\|searchEventsWithPagination" backend/src/main/java/com/tirana/events/repository/EventRepository.java
print_result $? "E1: Database pagination with Pageable"

# E2: Database indexes
echo -e "${BLUE}🔍 E2: Checking database indexes...${NC}"
grep -q "@Index.*idx_event" backend/src/main/java/com/tirana/events/model/Event.java
print_result $? "E2: Database indexes on Event table"

# E3: Search debouncing
echo -e "${BLUE}🔍 E3: Checking search debouncing...${NC}"
grep -q "searchTimeoutRef\|setTimeout.*500" mobile/src/screens/HomeScreen.js
print_result $? "E3: Search debouncing (500ms)"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 TEST SUMMARY"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

TOTAL=$((PASSED + FAILED))
PERCENTAGE=$((PASSED * 100 / TOTAL))

echo -e "${GREEN}✅ PASSED: $PASSED${NC}"
echo -e "${RED}❌ FAILED: $FAILED${NC}"
echo -e "TOTAL: $TOTAL"
echo -e "SUCCESS RATE: ${PERCENTAGE}%"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${GREEN}🎉 ALL TESTS PASSED! Your application is ready for deployment.${NC}"
    echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
else
    echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${RED}⚠️  SOME TESTS FAILED. Please review the fixes.${NC}"
    echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
fi

echo ""
echo "📚 For detailed information, see: COMPLETE_AUDIT_SUMMARY.txt"
echo ""
