import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
} from 'react-native';
import api from '../services/api';

export default function BadgesScreen() {
  const [badges, setBadges] = useState([]);

  useEffect(() => {
    loadBadges();
  }, []);

  const loadBadges = async () => {
    try {
      const response = await api.get('/badges/my-badges');
      setBadges(response.data);
    } catch (error) {
      console.error('Error loading badges:', error);
    }
  };

  const renderBadge = ({ item }) => (
    <View style={[styles.badgeCard, item.isNew && styles.badgeCardNew]}>
      {item.isNew && (
        <View style={styles.newBadge}>
          <Text style={styles.newBadgeText}>NEW!</Text>
        </View>
      )}
      <Text style={styles.badgeIcon}>{item.iconUrl}</Text>
      <Text style={styles.badgeName}>{item.badgeName}</Text>
      <Text style={styles.badgeDescription}>{item.description}</Text>
      <Text style={styles.badgeDate}>
        Earned {new Date(item.earnedAt).toLocaleDateString()}
      </Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Your Badges</Text>
        <Text style={styles.subtitle}>{badges.length} badges earned</Text>
      </View>

      <FlatList
        data={badges}
        renderItem={renderBadge}
        keyExtractor={(item) => item.id.toString()}
        numColumns={2}
        contentContainerStyle={styles.grid}
        columnWrapperStyle={styles.row}
        ListEmptyComponent={
          <View style={styles.emptyState}>
            <Text style={styles.emptyIcon}>🏆</Text>
            <Text style={styles.emptyText}>No badges yet</Text>
            <Text style={styles.emptySubtext}>
              Attend events to earn badges!
            </Text>
          </View>
        }
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
    padding: 20,
    paddingTop: 60,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 16,
    color: '#A0A0B0',
  },
  grid: {
    padding: 16,
  },
  row: {
    justifyContent: 'space-between',
  },
  badgeCard: {
    width: '48%',
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    alignItems: 'center',
  },
  badgeCardNew: {
    borderWidth: 2,
    borderColor: '#7F77DD',
  },
  newBadge: {
    position: 'absolute',
    top: 8,
    right: 8,
    backgroundColor: '#7F77DD',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 8,
  },
  newBadgeText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
  badgeIcon: {
    fontSize: 48,
    marginBottom: 12,
  },
  badgeName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    textAlign: 'center',
    marginBottom: 8,
  },
  badgeDescription: {
    fontSize: 12,
    color: '#A0A0B0',
    textAlign: 'center',
    marginBottom: 8,
  },
  badgeDate: {
    fontSize: 10,
    color: '#7F77DD',
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 60,
  },
  emptyIcon: {
    fontSize: 64,
    marginBottom: 16,
  },
  emptyText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#A0A0B0',
  },
});
