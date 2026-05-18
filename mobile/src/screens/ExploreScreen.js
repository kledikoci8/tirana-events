import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  TouchableOpacity,
  StatusBar,
  ScrollView,
  FlatList,
  Image,
} from 'react-native';
import MapView, { Marker } from 'react-native-maps';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import * as Location from 'expo-location';
import api from '../services/api';

const { width, height } = Dimensions.get('window');

export default function ExploreScreen({ navigation }) {
  const [events, setEvents] = useState([]);
  const [happeningNow, setHappeningNow] = useState([]);
  const [region, setRegion] = useState({
    latitude: 41.3275,
    longitude: 19.8187,
    latitudeDelta: 0.1,
    longitudeDelta: 0.1,
  });

  useEffect(() => {
    loadEvents();
    loadHappeningNow();
  }, []);

  const loadHappeningNow = async () => {
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      let params = '';
      if (status === 'granted') {
        const loc = await Location.getCurrentPositionAsync({});
        params = `?lat=${loc.coords.latitude}&lon=${loc.coords.longitude}`;
      }
      const res = await api.get(`/happening-now${params}`);
      setHappeningNow((res.data || []).slice(0, 8));
    } catch (e) {
      console.error(e);
    }
  };

  const loadEvents = async () => {
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status === 'granted') {
        const location = await Location.getCurrentPositionAsync({});
        const { latitude, longitude } = location.coords;
        setRegion((r) => ({ ...r, latitude, longitude }));
        const response = await api.get(
          `/events/nearby?lat=${latitude}&lng=${longitude}&radiusKm=15`
        );
        setEvents(response.data.filter((e) => e.latitude && e.longitude));
        return;
      }
    } catch (error) {
      console.error('Error loading nearby events:', error);
    }

    try {
      const response = await api.get('/events/upcoming');
      setEvents(response.data.filter((e) => e.latitude && e.longitude));
    } catch (error) {
      console.error('Error loading events:', error);
    }
  };

  const centerOnUser = async () => {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== 'granted') return;
    const location = await Location.getCurrentPositionAsync({});
    setRegion({
      latitude: location.coords.latitude,
      longitude: location.coords.longitude,
      latitudeDelta: 0.08,
      longitudeDelta: 0.08,
    });
    loadEvents();
  };

  const mapStyle = [
    { elementType: 'geometry', stylers: [{ color: '#1A1A24' }] },
    { elementType: 'labels.text.fill', stylers: [{ color: '#9CA3AF' }] },
    { featureType: 'road', elementType: 'geometry', stylers: [{ color: '#2A2A3C' }] },
    { featureType: 'water', elementType: 'geometry', stylers: [{ color: '#0A0A0F' }] },
  ];

  const renderNowItem = ({ item }) => (
    <TouchableOpacity
      style={styles.nowCard}
      onPress={() => navigation.navigate('EventDetail', { eventId: item.id })}
    >
      <Image source={{ uri: item.imageUrl }} style={styles.nowImage} />
      <LinearGradient colors={['transparent', '#000']} style={styles.nowGradient} />
      <Text style={styles.nowTitle} numberOfLines={1}>{item.name}</Text>
      <Text style={styles.nowMeta}>{item.minutesUntilStart}m · {item.capacityStatus}</Text>
    </TouchableOpacity>
  );

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
            coordinate={{ latitude: event.latitude, longitude: event.longitude }}
            onPress={() => navigation.navigate('EventDetail', { eventId: event.id })}
          >
            <View style={styles.markerContainer}>
              <LinearGradient colors={['#8B5CF6', '#6D28D9']} style={styles.marker}>
                <Ionicons name="musical-notes" size={16} color="#FFFFFF" />
              </LinearGradient>
            </View>
          </Marker>
        ))}
      </MapView>

      <View style={styles.header}>
        <LinearGradient colors={['rgba(10,10,15,0.95)', 'transparent']} style={styles.headerGradient}>
          <View style={styles.headerRow}>
            <View>
              <Text style={styles.title}>Explore</Text>
              <Text style={styles.subtitle}>Events around you</Text>
            </View>
            <TouchableOpacity
              style={styles.iconBtn}
              onPress={() =>
                navigation.navigate('Filter', {
                  onApplyFilters: (data) =>
                    setEvents(data.filter((e) => e.latitude && e.longitude)),
                })
              }
            >
              <Ionicons name="options" size={22} color="#FFF" />
            </TouchableOpacity>
          </View>

          {happeningNow.length > 0 && (
            <View style={styles.nowSection}>
              <View style={styles.nowHeader}>
                <Text style={styles.nowLabel}>Happening now</Text>
                <TouchableOpacity onPress={() => navigation.navigate('HappeningNow')}>
                  <Text style={styles.seeAll}>See all</Text>
                </TouchableOpacity>
              </View>
              <FlatList
                horizontal
                data={happeningNow}
                renderItem={renderNowItem}
                keyExtractor={(i) => String(i.id)}
                showsHorizontalScrollIndicator={false}
              />
            </View>
          )}
        </LinearGradient>
      </View>

      <TouchableOpacity style={styles.myLocationButton} onPress={centerOnUser}>
        <LinearGradient colors={['#8B5CF6', '#6D28D9']} style={styles.myLocationGradient}>
          <Ionicons name="locate" size={24} color="#FFFFFF" />
        </LinearGradient>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0A0A0F' },
  map: { width, height },
  header: { position: 'absolute', top: 0, left: 0, right: 0 },
  headerGradient: { paddingTop: 56, paddingHorizontal: 20, paddingBottom: 16 },
  headerRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start' },
  title: { fontSize: 28, fontWeight: 'bold', color: '#FFF' },
  subtitle: { fontSize: 14, color: '#9CA3AF', marginTop: 4 },
  iconBtn: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: '#1F1F2E',
    justifyContent: 'center',
    alignItems: 'center',
  },
  nowSection: { marginTop: 16 },
  nowHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  nowLabel: { color: '#EF4444', fontWeight: '700', fontSize: 14 },
  seeAll: { color: '#8B5CF6', fontSize: 13 },
  nowCard: {
    width: 140,
    height: 90,
    marginRight: 10,
    borderRadius: 12,
    overflow: 'hidden',
    backgroundColor: '#1F1F2E',
  },
  nowImage: { width: '100%', height: '100%' },
  nowGradient: { ...StyleSheet.absoluteFillObject },
  nowTitle: {
    position: 'absolute',
    bottom: 22,
    left: 8,
    right: 8,
    color: '#FFF',
    fontSize: 12,
    fontWeight: '600',
  },
  nowMeta: { position: 'absolute', bottom: 6, left: 8, color: '#9CA3AF', fontSize: 10 },
  markerContainer: { alignItems: 'center' },
  marker: {
    width: 40,
    height: 40,
    borderRadius: 20,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 3,
    borderColor: '#FFF',
  },
  myLocationButton: {
    position: 'absolute',
    bottom: 120,
    right: 20,
    borderRadius: 28,
    overflow: 'hidden',
  },
  myLocationGradient: { width: 56, height: 56, justifyContent: 'center', alignItems: 'center' },
});
