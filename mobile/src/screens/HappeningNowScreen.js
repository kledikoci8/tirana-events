import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Image,
  TouchableOpacity,
  RefreshControl,
} from 'react-native';
import * as Location from 'expo-location';

const API_URL = 'http://192.168.1.6:8080/api';

export default function HappeningNowScreen({ navigation }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [location, setLocation] = useState(null);

  useEffect(() => {
    getLocation();
    loadEvents();
  }, []);

  const getLocation = async () => {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status === 'granted') {
      const loc = await Location.getCurrentPositionAsync({});
      setLocation(loc.coords);
    }
  };

  const loadEvents = async () => {
    try {
      const params = location ? `?lat=${location.latitude}&lon=${location.longitude}` : '';
      const response = await fetch(`${API_URL}/happening-now${params}`);
      const data = await response.json();
      setEvents(data);
    } catch (error) {
      console.error('Error loading events:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderEvent = ({ item }) => (
    <TouchableOpacity
      style={styles.eventCard}
      onPress={() => navigation.navigate('EventDetail', { eventId: item.id })}
    >
      <Image source={{ uri: item.imageUrl }} style={styles.eventImage} />
      
      <View style={styles.urgencyBadge}>
        <Text style={styles.urgencyText}>
          {item.minutesUntilStart < 60
            ? `${item.minutesUntilStart}m`
            : `${Math.floor(item.minutesUntilStart / 60)}h ${item.minutesUntilStart % 60}m`}
        </Text>
      </View>

      {item.discountPercentage && (
        <View style={styles.discountBadge}>
          <Text style={styles.discountText}>{item.discountPercentage}% OFF</Text>
        </View>
      )}

      <View style={styles.eventInfo}>
        <Text style={styles.eventName}>{item.name}</Text>
        <Text style={styles.venue}>{item.venue}</Text>

        <View style={styles.priceRow}>
          {item.lastMinutePrice ? (
            <>
              <Text style={styles.originalPrice}>{item.price} ALL</Text>
              <Text style={styles.salePrice}>{item.lastMinutePrice} ALL</Text>
            </>
          ) : (
            <Text style={styles.price}>{item.price} ALL</Text>
          )}
        </View>

        <View style={styles.capacityRow}>
          <View style={[styles.capacityDot, { backgroundColor: getCapacityColor(item.capacityStatus) }]} />
          <Text style={styles.capacityText}>
            {item.ticketsRemaining} tickets left
          </Text>
        </View>

        {item.distance && (
          <Text style={styles.distance}>{item.distance.toFixed(1)} km away</Text>
        )}
      </View>
    </TouchableOpacity>
  );

  const getCapacityColor = (status) => {
    switch (status) {
      case 'LOW': return '#F44336';
      case 'MEDIUM': return '#FF9800';
      case 'HIGH': return '#4CAF50';
      default: return '#A0A0B0';
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Happening Now</Text>
        <Text style={styles.subtitle}>Events starting in the next 2 hours</Text>
      </View>

      <FlatList
        data={events}
        renderItem={renderEvent}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
        refreshControl={
          <RefreshControl refreshing={loading} onRefresh={loadEvents} tintColor="#7F77DD" />
        }
        ListEmptyComponent={
          <View style={styles.emptyState}>
            <Text style={styles.emptyText}>No events starting soon</Text>
            <Text style={styles.emptySubtext}>Check back later for last-minute deals!</Text>
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
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#A0A0B0',
  },
  list: {
    padding: 16,
  },
  eventCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    marginBottom: 16,
    overflow: 'hidden',
  },
  eventImage: {
    width: '100%',
    height: 200,
  },
  urgencyBadge: {
    position: 'absolute',
    top: 12,
    right: 12,
    backgroundColor: '#FF5252',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
  },
  urgencyText: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  discountBadge: {
    position: 'absolute',
    top: 12,
    left: 12,
    backgroundColor: '#4CAF50',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
  },
  discountText: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  eventInfo: {
    padding: 16,
  },
  eventName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  venue: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 12,
  },
  priceRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  price: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#7F77DD',
  },
  originalPrice: {
    fontSize: 16,
    color: '#A0A0B0',
    textDecorationLine: 'line-through',
    marginRight: 8,
  },
  salePrice: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#4CAF50',
  },
  capacityRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  capacityDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: 8,
  },
  capacityText: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  distance: {
    fontSize: 14,
    color: '#7F77DD',
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 60,
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
