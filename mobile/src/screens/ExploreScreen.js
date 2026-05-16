import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import MapView, { Marker } from 'react-native-maps';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import api from '../services/api';

const { width, height } = Dimensions.get('window');

export default function ExploreScreen({ navigation }) {
  const [events, setEvents] = useState([]);
  const [region, setRegion] = useState({
    latitude: 41.3275,
    longitude: 19.8187,
    latitudeDelta: 0.1,
    longitudeDelta: 0.1,
  });

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const response = await api.get('/events/upcoming');
      setEvents(response.data.filter(e => e.latitude && e.longitude));
    } catch (error) {
      console.error('Error loading events:', error);
    }
  };

  const mapStyle = [
    {
      elementType: 'geometry',
      stylers: [{ color: '#1A1A24' }],
    },
    {
      elementType: 'labels.text.fill',
      stylers: [{ color: '#9CA3AF' }],
    },
    {
      elementType: 'labels.text.stroke',
      stylers: [{ color: '#0A0A0F' }],
    },
    {
      featureType: 'road',
      elementType: 'geometry',
      stylers: [{ color: '#2A2A3C' }],
    },
    {
      featureType: 'water',
      elementType: 'geometry',
      stylers: [{ color: '#0A0A0F' }],
    },
  ];

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      <MapView
        style={styles.map}
        region={region}
        onRegionChangeComplete={setRegion}
        customMapStyle={mapStyle}
      >
        {events.map((event) => (
          <Marker
            key={event.id}
            coordinate={{
              latitude: event.latitude,
              longitude: event.longitude,
            }}
            onPress={() => navigation.navigate('EventDetail', { eventId: event.id })}
          >
            <View style={styles.markerContainer}>
              <LinearGradient
                colors={['#8B5CF6', '#6D28D9']}
                style={styles.marker}
              >
                <Ionicons name="musical-notes" size={16} color="#FFFFFF" />
              </LinearGradient>
            </View>
          </Marker>
        ))}
      </MapView>

      <View style={styles.header}>
        <LinearGradient
          colors={['rgba(10,10,15,0.9)', 'transparent']}
          style={styles.headerGradient}
        >
          <Text style={styles.title}>Explore</Text>
          <Text style={styles.subtitle}>Events around you</Text>
        </LinearGradient>
      </View>

      <TouchableOpacity style={styles.myLocationButton}>
        <LinearGradient
          colors={['#8B5CF6', '#6D28D9']}
          style={styles.myLocationGradient}
        >
          <Ionicons name="locate" size={24} color="#FFFFFF" />
        </LinearGradient>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  map: {
    width,
    height,
  },
  header: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
  },
  headerGradient: {
    paddingTop: 60,
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  subtitle: {
    fontSize: 16,
    color: '#9CA3AF',
    marginTop: 4,
  },
  markerContainer: {
    alignItems: 'center',
  },
  marker: {
    width: 40,
    height: 40,
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 3,
    borderColor: '#FFFFFF',
  },
  myLocationButton: {
    position: 'absolute',
    bottom: 120,
    right: 20,
    borderRadius: 28,
    overflow: 'hidden',
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },
  myLocationGradient: {
    width: 56,
    height: 56,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
