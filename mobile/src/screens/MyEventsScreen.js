import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import api from '../services/api';

export default function MyEventsScreen({ navigation }) {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    try {
      const res = await api.get('/events/mine');
      setEvents(res.data || []);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <LinearGradient colors={['#0A0A0F', '#1A1A24']} style={styles.flex}>
        <View style={styles.header}>
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={24} color="#FFF" />
          </TouchableOpacity>
          <Text style={styles.title}>My Events</Text>
          <View style={{ width: 24 }} />
        </View>
        <FlatList
          data={events}
          keyExtractor={(item) => String(item.id)}
          contentContainerStyle={styles.list}
          ListEmptyComponent={
            <Text style={styles.empty}>Create an event to see analytics and manage it here.</Text>
          }
          renderItem={({ item }) => (
            <TouchableOpacity
              style={styles.card}
              onPress={() =>
                navigation.navigate('OrganizerDashboard', { eventId: item.id, eventName: item.name })
              }
            >
              <Text style={styles.name}>{item.name}</Text>
              <Text style={styles.meta}>
                {new Date(item.startDate).toLocaleDateString()} · {item.currentAttendees || 0} tickets
              </Text>
            </TouchableOpacity>
          )}
        />
      </LinearGradient>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0A0A0F' },
  flex: { flex: 1 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 56,
    paddingHorizontal: 20,
    paddingBottom: 16,
  },
  title: { fontSize: 20, fontWeight: 'bold', color: '#FFF' },
  list: { padding: 20, paddingBottom: 40 },
  card: {
    backgroundColor: '#1F1F2E',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  name: { fontSize: 16, fontWeight: '600', color: '#FFF' },
  meta: { fontSize: 13, color: '#9CA3AF', marginTop: 4 },
  empty: { color: '#6B7280', textAlign: 'center', marginTop: 40 },
});
