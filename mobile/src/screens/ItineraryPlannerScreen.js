import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  Share,
} from 'react-native';
import api from '../services/api';

export default function ItineraryPlannerScreen({ navigation }) {
  const [itineraries, setItineraries] = useState([]);

  useEffect(() => {
    loadItineraries();
  }, []);

  const loadItineraries = async () => {
    try {
      const response = await api.get('/itineraries/my-itineraries');
      setItineraries(response.data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const shareItinerary = async (itinerary) => {
    try {
      await Share.share({
        message: `Check out my event itinerary: ${itinerary.shareUrl}`,
        title: itinerary.name,
      });
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const renderItinerary = ({ item }) => (
    <TouchableOpacity
      style={styles.itineraryCard}
      onPress={() => navigation.navigate('ItineraryDetail', { itineraryId: item.id })}
    >
      <View style={styles.itineraryHeader}>
        <Text style={styles.itineraryName}>{item.name}</Text>
        {item.hasOverlaps && (
          <View style={styles.warningBadge}>
            <Text style={styles.warningText}>⚠️ Overlaps</Text>
          </View>
        )}
      </View>

      {item.description && (
        <Text style={styles.itineraryDescription}>{item.description}</Text>
      )}

      <View style={styles.eventsPreview}>
        {item.events.slice(0, 3).map((event, index) => (
          <View key={event.id} style={styles.eventPreview}>
            <Image source={{ uri: event.imageUrl }} style={styles.eventThumb} />
            <Text style={styles.eventName} numberOfLines={1}>
              {event.name}
            </Text>
          </View>
        ))}
        {item.events.length > 3 && (
          <Text style={styles.moreEvents}>+{item.events.length - 3} more</Text>
        )}
      </View>

      <View style={styles.itineraryFooter}>
        <Text style={styles.itineraryStats}>
          {item.events.length} events • {Math.floor(item.totalDuration / 60)}h {item.totalDuration % 60}m
        </Text>
        <TouchableOpacity
          style={styles.shareButton}
          onPress={() => shareItinerary(item)}
        >
          <Text style={styles.shareIcon}>🔗</Text>
        </TouchableOpacity>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>My Itineraries</Text>
        <TouchableOpacity
          style={styles.createButton}
          onPress={() => navigation.navigate('CreateItinerary')}
        >
          <Text style={styles.createButtonText}>+ New</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={itineraries}
        renderItem={renderItinerary}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
        ListEmptyComponent={
          <View style={styles.emptyState}>
            <Text style={styles.emptyIcon}>📅</Text>
            <Text style={styles.emptyText}>No itineraries yet</Text>
            <Text style={styles.emptySubtext}>
              Plan your perfect weekend!
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
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    paddingTop: 60,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  createButton: {
    backgroundColor: '#7F77DD',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 20,
  },
  createButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
  list: {
    padding: 20,
  },
  itineraryCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
  },
  itineraryHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  itineraryName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    flex: 1,
  },
  warningBadge: {
    backgroundColor: '#FF9800',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 8,
  },
  warningText: {
    color: '#FFFFFF',
    fontSize: 12,
    fontWeight: 'bold',
  },
  itineraryDescription: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 16,
  },
  eventsPreview: {
    flexDirection: 'row',
    marginBottom: 16,
    gap: 8,
  },
  eventPreview: {
    alignItems: 'center',
  },
  eventThumb: {
    width: 60,
    height: 60,
    borderRadius: 8,
    marginBottom: 4,
  },
  eventName: {
    fontSize: 10,
    color: '#A0A0B0',
    width: 60,
    textAlign: 'center',
  },
  moreEvents: {
    fontSize: 14,
    color: '#7F77DD',
    alignSelf: 'center',
  },
  itineraryFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  itineraryStats: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  shareButton: {
    padding: 8,
  },
  shareIcon: {
    fontSize: 20,
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
