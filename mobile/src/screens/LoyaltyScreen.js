import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
} from 'react-native';
import api from '../services/api';

const TIER_COLORS = {
  BRONZE: '#CD7F32',
  SILVER: '#C0C0C0',
  GOLD: '#FFD700',
  VIP: '#9B59B6',
};

export default function LoyaltyScreen() {
  const [tierInfo, setTierInfo] = useState(null);
  const [pointsHistory, setPointsHistory] = useState([]);

  useEffect(() => {
    loadTierInfo();
    loadPointsHistory();
  }, []);

  const loadTierInfo = async () => {
    try {
      const response = await api.get('/loyalty/tier');
      setTierInfo(response.data);
    } catch (error) {
      console.error('Error loading tier:', error);
    }
  };

  const loadPointsHistory = async () => {
    try {
      const response = await api.get('/loyalty/points/history');
      setPointsHistory(response.data);
    } catch (error) {
      console.error('Error loading history:', error);
    }
  };

  const renderPointsItem = ({ item }) => (
    <View style={styles.pointsCard}>
      <View style={styles.pointsLeft}>
        <Text style={[styles.pointsAmount, item.points > 0 ? styles.pointsPositive : styles.pointsNegative]}>
          {item.points > 0 ? '+' : ''}{item.points}
        </Text>
        <View>
          <Text style={styles.pointsAction}>{item.action}</Text>
          <Text style={styles.pointsDescription}>{item.description}</Text>
          <Text style={styles.pointsDate}>
            {new Date(item.earnedAt).toLocaleDateString()}
          </Text>
        </View>
      </View>
    </View>
  );

  if (!tierInfo) {
    return (
      <View style={styles.container}>
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  const tierColor = TIER_COLORS[tierInfo.tier];
  const progressPercentage = tierInfo.pointsToNextTier > 0
    ? ((tierInfo.totalPoints / (tierInfo.totalPoints + tierInfo.pointsToNextTier)) * 100)
    : 100;

  return (
    <View style={styles.container}>
      <View style={[styles.header, { backgroundColor: tierColor }]}>
        <Text style={styles.tierBadge}>{tierInfo.tier}</Text>
        <Text style={styles.tierTitle}>Tier Member</Text>
        <Text style={styles.pointsTotal}>{tierInfo.totalPoints} Points</Text>
      </View>

      <View style={styles.statsContainer}>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{tierInfo.currentStreak}</Text>
          <Text style={styles.statLabel}>Day Streak</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{tierInfo.longestStreak}</Text>
          <Text style={styles.statLabel}>Best Streak</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{tierInfo.lifetimePoints}</Text>
          <Text style={styles.statLabel}>Lifetime</Text>
        </View>
      </View>

      {tierInfo.tier !== 'VIP' && (
        <View style={styles.progressContainer}>
          <View style={styles.progressHeader}>
            <Text style={styles.progressText}>
              {tierInfo.pointsToNextTier} points to {tierInfo.nextTier}
            </Text>
          </View>
          <View style={styles.progressBar}>
            <View style={[styles.progressFill, { width: `${progressPercentage}%`, backgroundColor: tierColor }]} />
          </View>
        </View>
      )}

      <View style={styles.benefitsContainer}>
        <Text style={styles.sectionTitle}>Your Benefits</Text>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitIcon}>🎟️</Text>
          <Text style={styles.benefitText}>100 points per ticket purchase</Text>
        </View>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitIcon}>⭐</Text>
          <Text style={styles.benefitText}>50 points per review</Text>
        </View>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitIcon}>🔥</Text>
          <Text style={styles.benefitText}>10 points per streak day</Text>
        </View>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitIcon}>👥</Text>
          <Text style={styles.benefitText}>200 points per referral</Text>
        </View>
      </View>

      <Text style={styles.sectionTitle}>Points History</Text>
      <FlatList
        data={pointsHistory}
        renderItem={renderPointsItem}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  header: {
    padding: 40,
    paddingTop: 80,
    alignItems: 'center',
    borderBottomLeftRadius: 24,
    borderBottomRightRadius: 24,
  },
  tierBadge: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  tierTitle: {
    fontSize: 20,
    color: '#FFFFFF',
    marginBottom: 16,
  },
  pointsTotal: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  statsContainer: {
    flexDirection: 'row',
    padding: 16,
    gap: 12,
  },
  statCard: {
    flex: 1,
    backgroundColor: '#1A1A24',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  statValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#7F77DD',
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
    color: '#A0A0B0',
  },
  progressContainer: {
    padding: 16,
  },
  progressHeader: {
    marginBottom: 8,
  },
  progressText: {
    fontSize: 14,
    color: '#FFFFFF',
  },
  progressBar: {
    height: 8,
    backgroundColor: '#1A1A24',
    borderRadius: 4,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    borderRadius: 4,
  },
  benefitsContainer: {
    padding: 16,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
    paddingHorizontal: 16,
  },
  benefitCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1A1A24',
    padding: 16,
    borderRadius: 12,
    marginBottom: 8,
  },
  benefitIcon: {
    fontSize: 24,
    marginRight: 12,
  },
  benefitText: {
    fontSize: 16,
    color: '#FFFFFF',
  },
  list: {
    padding: 16,
  },
  pointsCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  pointsLeft: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  pointsAmount: {
    fontSize: 24,
    fontWeight: 'bold',
    marginRight: 16,
    minWidth: 60,
  },
  pointsPositive: {
    color: '#4CAF50',
  },
  pointsNegative: {
    color: '#F44336',
  },
  pointsAction: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  pointsDescription: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 4,
  },
  pointsDate: {
    fontSize: 12,
    color: '#7F77DD',
  },
  loadingText: {
    fontSize: 18,
    color: '#FFFFFF',
    textAlign: 'center',
    marginTop: 100,
  },
});
