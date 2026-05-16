import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
  StatusBar,
  Share,
  Alert,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const OrganizerDashboardScreen = ({ route, navigation }) => {
  const { eventId, eventName } = route.params;
  const [analytics, setAnalytics] = useState(null);
  const [funnel, setFunnel] = useState(null);
  const [traffic, setTraffic] = useState(null);
  const [revenue, setRevenue] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchAnalytics();
    const interval = setInterval(fetchAnalytics, 30000); // Update every 30 seconds
    return () => clearInterval(interval);
  }, []);

  const fetchAnalytics = async () => {
    try {
      const token = await AsyncStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const [overviewRes, funnelRes, trafficRes, revenueRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/analytics/events/${eventId}/overview`, { headers }),
        axios.get(`http://localhost:8080/api/analytics/events/${eventId}/funnel`, { headers }),
        axios.get(`http://localhost:8080/api/analytics/events/${eventId}/traffic`, { headers }),
        axios.get(`http://localhost:8080/api/analytics/events/${eventId}/revenue`, { headers }),
      ]);

      setAnalytics(overviewRes.data);
      setFunnel(funnelRes.data);
      setTraffic(trafficRes.data);
      setRevenue(revenueRes.data);
    } catch (error) {
      console.error('Error fetching analytics:', error);
      Alert.alert('Error', 'Failed to load analytics. Make sure you are the event organizer.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const onRefresh = () => {
    setRefreshing(true);
    fetchAnalytics();
  };

  const exportCSV = async () => {
    try {
      const token = await AsyncStorage.getItem('token');
      const response = await axios.get(
        `http://localhost:8080/api/analytics/events/${eventId}/export`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // In a real app, you'd save this to a file or share it
      Share.share({
        message: response.data,
        title: `${eventName} Analytics`,
      });
    } catch (error) {
      console.error('Error exporting CSV:', error);
      Alert.alert('Error', 'Failed to export analytics');
    }
  };

  const getTrafficPercentage = (source) => {
    if (!traffic) return 0;
    const total = Object.values(traffic).reduce((sum, val) => sum + val, 0);
    return total > 0 ? ((traffic[source] / total) * 100).toFixed(1) : 0;
  };

  if (loading) {
    return (
      <View style={[styles.container, styles.centerContent]}>
        <Text style={styles.loadingText}>Loading analytics...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <View style={styles.headerInfo}>
          <Text style={styles.headerTitle}>Analytics</Text>
          <Text style={styles.headerSubtitle}>{eventName}</Text>
        </View>
        <TouchableOpacity onPress={exportCSV}>
          <Ionicons name="download" size={24} color="#7F77DD" />
        </TouchableOpacity>
      </View>

      <ScrollView
        style={styles.content}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#7F77DD" />
        }
      >
        {/* Live Stats Cards */}
        <View style={styles.statsGrid}>
          <View style={styles.statCard}>
            <LinearGradient colors={['#7F77DD', '#9B8FE8']} style={styles.statGradient}>
              <Ionicons name="eye" size={24} color="#FFFFFF" />
              <Text style={styles.statValue}>{analytics?.totalViews || 0}</Text>
              <Text style={styles.statLabel}>Total Views</Text>
            </LinearGradient>
          </View>

          <View style={styles.statCard}>
            <LinearGradient colors={['#FF6B6B', '#FF8E8E']} style={styles.statGradient}>
              <Ionicons name="ticket" size={24} color="#FFFFFF" />
              <Text style={styles.statValue}>{analytics?.completedPurchases || 0}</Text>
              <Text style={styles.statLabel}>Tickets Sold</Text>
            </LinearGradient>
          </View>

          <View style={styles.statCard}>
            <LinearGradient colors={['#4CAF50', '#66BB6A']} style={styles.statGradient}>
              <Ionicons name="cash" size={24} color="#FFFFFF" />
              <Text style={styles.statValue}>{analytics?.totalRevenue || 0} ALL</Text>
              <Text style={styles.statLabel}>Total Revenue</Text>
            </LinearGradient>
          </View>

          <View style={styles.statCard}>
            <LinearGradient colors={['#FF9800', '#FFB74D']} style={styles.statGradient}>
              <Ionicons name="trending-up" size={24} color="#FFFFFF" />
              <Text style={styles.statValue}>
                {funnel?.overallConversionRate?.toFixed(1) || 0}%
              </Text>
              <Text style={styles.statLabel}>Conversion</Text>
            </LinearGradient>
          </View>
        </View>

        {/* Revenue Breakdown */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Revenue Breakdown</Text>
          <View style={styles.revenueCard}>
            <View style={styles.revenueRow}>
              <Text style={styles.revenueLabel}>Daily Revenue</Text>
              <Text style={styles.revenueValue}>{revenue?.daily || 0} ALL</Text>
            </View>
            <View style={styles.revenueRow}>
              <Text style={styles.revenueLabel}>Weekly Revenue</Text>
              <Text style={styles.revenueValue}>{revenue?.weekly || 0} ALL</Text>
            </View>
            <View style={styles.revenueRow}>
              <Text style={styles.revenueLabel}>Total Revenue</Text>
              <Text style={[styles.revenueValue, styles.revenueTotal]}>
                {revenue?.total || 0} ALL
              </Text>
            </View>
          </View>
        </View>

        {/* Sales Funnel */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Sales Funnel</Text>
          <View style={styles.funnelCard}>
            <FunnelStep
              label="Views"
              value={funnel?.views || 0}
              percentage={100}
              color="#7F77DD"
            />
            <FunnelStep
              label="Saves"
              value={funnel?.saves || 0}
              percentage={funnel?.viewToSaveRate || 0}
              color="#9B8FE8"
            />
            <FunnelStep
              label="Ticket Page"
              value={funnel?.ticketPageViews || 0}
              percentage={funnel?.saveToTicketRate || 0}
              color="#B5A3F3"
            />
            <FunnelStep
              label="Purchases"
              value={funnel?.completedPurchases || 0}
              percentage={funnel?.ticketToPurchaseRate || 0}
              color="#CFB7FF"
            />
          </View>
        </View>

        {/* Traffic Sources */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Traffic Sources</Text>
          <View style={styles.trafficCard}>
            <TrafficSource
              icon="search"
              label="Search"
              value={traffic?.search || 0}
              percentage={getTrafficPercentage('search')}
            />
            <TrafficSource
              icon="home"
              label="Home Feed"
              value={traffic?.homeFeed || 0}
              percentage={getTrafficPercentage('homeFeed')}
            />
            <TrafficSource
              icon="map"
              label="Map"
              value={traffic?.map || 0}
              percentage={getTrafficPercentage('map')}
            />
            <TrafficSource
              icon="people"
              label="Friend Share"
              value={traffic?.friendShare || 0}
              percentage={getTrafficPercentage('friendShare')}
            />
            <TrafficSource
              icon="link"
              label="Direct Link"
              value={traffic?.directLink || 0}
              percentage={getTrafficPercentage('directLink')}
            />
          </View>
        </View>

        {/* Last Updated */}
        <Text style={styles.lastUpdated}>
          Last updated: {new Date(analytics?.lastUpdated).toLocaleTimeString()}
        </Text>
      </ScrollView>
    </View>
  );
};

const FunnelStep = ({ label, value, percentage, color }) => (
  <View style={styles.funnelStep}>
    <View style={styles.funnelStepHeader}>
      <Text style={styles.funnelStepLabel}>{label}</Text>
      <Text style={styles.funnelStepValue}>{value}</Text>
    </View>
    <View style={styles.funnelBar}>
      <View style={[styles.funnelBarFill, { width: `${percentage}%`, backgroundColor: color }]} />
    </View>
    <Text style={styles.funnelStepPercentage}>{percentage.toFixed(1)}%</Text>
  </View>
);

const TrafficSource = ({ icon, label, value, percentage }) => (
  <View style={styles.trafficRow}>
    <View style={styles.trafficInfo}>
      <Ionicons name={icon} size={20} color="#7F77DD" />
      <Text style={styles.trafficLabel}>{label}</Text>
    </View>
    <View style={styles.trafficStats}>
      <Text style={styles.trafficValue}>{value}</Text>
      <Text style={styles.trafficPercentage}>{percentage}%</Text>
    </View>
  </View>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  centerContent: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: '#A0A0B0',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#1A1A24',
  },
  headerInfo: {
    flex: 1,
    alignItems: 'center',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#A0A0B0',
    marginTop: 2,
  },
  content: {
    flex: 1,
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: 12,
  },
  statCard: {
    width: '48%',
    margin: '1%',
    borderRadius: 16,
    overflow: 'hidden',
  },
  statGradient: {
    padding: 20,
    alignItems: 'center',
  },
  statValue: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginTop: 8,
  },
  statLabel: {
    fontSize: 12,
    color: '#FFFFFF',
    opacity: 0.9,
    marginTop: 4,
  },
  section: {
    padding: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
  },
  revenueCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
  },
  revenueRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#252535',
  },
  revenueLabel: {
    fontSize: 16,
    color: '#A0A0B0',
  },
  revenueValue: {
    fontSize: 18,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  revenueTotal: {
    fontSize: 20,
    color: '#4CAF50',
  },
  funnelCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
  },
  funnelStep: {
    marginBottom: 20,
  },
  funnelStepHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  funnelStepLabel: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  funnelStepValue: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFFFFF',
  },
  funnelBar: {
    height: 8,
    backgroundColor: '#252535',
    borderRadius: 4,
    overflow: 'hidden',
    marginBottom: 4,
  },
  funnelBarFill: {
    height: '100%',
    borderRadius: 4,
  },
  funnelStepPercentage: {
    fontSize: 12,
    color: '#7F77DD',
    textAlign: 'right',
  },
  trafficCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
  },
  trafficRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#252535',
  },
  trafficInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  trafficLabel: {
    fontSize: 16,
    color: '#FFFFFF',
    marginLeft: 12,
  },
  trafficStats: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  trafficValue: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFFFFF',
    marginRight: 12,
  },
  trafficPercentage: {
    fontSize: 14,
    color: '#7F77DD',
    minWidth: 50,
    textAlign: 'right',
  },
  lastUpdated: {
    fontSize: 12,
    color: '#A0A0B0',
    textAlign: 'center',
    paddingVertical: 20,
  },
});

export default OrganizerDashboardScreen;
