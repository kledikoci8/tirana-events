import React, { useEffect, useState } from 'react';
import api from '../services/api';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';

const SECTIONS = [
  {
    title: 'Priority 1',
    items: [
      { icon: 'people', label: 'Friends & activity', screen: 'Friends' },
      { icon: 'notifications', label: 'Notification settings', screen: 'NotificationSettings' },
      { icon: 'mail', label: 'In-app notifications', screen: 'NotificationsInbox' },
    ],
  },
  {
    title: 'Discover',
    items: [
      { icon: 'flash', label: 'Happening now', screen: 'HappeningNow' },
      { icon: 'sparkles', label: 'Mood search', screen: 'MoodSearch' },
      { icon: 'star', label: 'Curated lists', screen: 'Curators' },
    ],
  },
  {
    title: 'Tickets & rewards',
    items: [
      { icon: 'people-circle', label: 'Group tickets', screen: 'GroupTickets', params: {} },
      { icon: 'ribbon', label: 'Loyalty & points', screen: 'Loyalty' },
      { icon: 'medal', label: 'Badges', screen: 'Badges' },
    ],
  },
  {
    title: 'Community',
    items: [
      { icon: 'chatbubbles', label: 'Community boards', screen: 'Community', params: { boardType: 'GENERAL' } },
      { icon: 'calendar', label: 'Weekend planner', screen: 'Itineraries' },
    ],
  },
  {
    title: 'Organiser',
    items: [
      { icon: 'analytics', label: 'My events & analytics', screen: 'MyEvents' },
    ],
  },
];

export default function ProfileScreen({ navigation }) {
  const { user, logout } = useAuth();
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const response = await api.get('/users/me');
      setProfile(response.data);
    } catch (error) {
      console.error('Error loading profile:', error);
    }
  };

  const display = profile || user;

  const interests = (display?.interests || []).map((name, index) => ({
    id: index,
    name,
    color: '#8B5CF6',
  }));

  const go = (screen, params) => navigation.navigate(screen, params);

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <LinearGradient colors={['#0A0A0F', '#1A1A24']} style={styles.gradient}>
        <ScrollView showsVerticalScrollIndicator={false}>
          <View style={styles.header}>
            <Text style={styles.headerTitle}>Profile</Text>
          </View>

          <View style={styles.profileSection}>
            <LinearGradient colors={['#8B5CF6', '#EC4899']} style={styles.avatarGradient}>
              <Text style={styles.avatarText}>{display?.fullName?.charAt(0) || 'U'}</Text>
            </LinearGradient>
            <Text style={styles.name}>{display?.fullName || 'User'}</Text>
            <Text style={styles.email}>{display?.email}</Text>

            <View style={styles.stats}>
              <View style={styles.statItem}>
                <Text style={styles.statValue}>{display?.eventsCount ?? 0}</Text>
                <Text style={styles.statLabel}>Events</Text>
              </View>
              <View style={styles.statDivider} />
              <View style={styles.statItem}>
                <Text style={styles.statValue}>{display?.ticketsCount ?? 0}</Text>
                <Text style={styles.statLabel}>Tickets</Text>
              </View>
              <View style={styles.statDivider} />
              <View style={styles.statItem}>
                <Text style={styles.statValue}>{display?.savedCount ?? 0}</Text>
                <Text style={styles.statLabel}>Saved</Text>
              </View>
            </View>
          </View>

          {interests.length > 0 && (
            <View style={styles.section}>
              <Text style={styles.sectionTitle}>My interests</Text>
              <View style={styles.interestsContainer}>
                {interests.map((interest) => (
                  <View key={interest.id} style={[styles.interestChip, { backgroundColor: interest.color + '20' }]}>
                    <Text style={[styles.interestText, { color: interest.color }]}>{interest.name}</Text>
                  </View>
                ))}
              </View>
            </View>
          )}

          {SECTIONS.map((section) => (
            <View key={section.title} style={styles.section}>
              <Text style={styles.sectionTitle}>{section.title}</Text>
              {section.items.map((item) => (
                <TouchableOpacity
                  key={item.label}
                  style={styles.menuItem}
                  onPress={() => go(item.screen, item.params)}
                >
                  <View style={styles.menuItemLeft}>
                    <View style={styles.menuIcon}>
                      <Ionicons name={item.icon} size={20} color="#8B5CF6" />
                    </View>
                    <Text style={styles.menuLabel}>{item.label}</Text>
                  </View>
                  <Ionicons name="chevron-forward" size={20} color="#6B7280" />
                </TouchableOpacity>
              ))}
            </View>
          ))}

          <TouchableOpacity style={styles.logoutButton} onPress={logout}>
            <Ionicons name="log-out-outline" size={20} color="#EF4444" />
            <Text style={styles.logoutText}>Logout</Text>
          </TouchableOpacity>
        </ScrollView>
      </LinearGradient>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0A0A0F' },
  gradient: { flex: 1 },
  header: { paddingHorizontal: 20, paddingTop: 60, paddingBottom: 8 },
  headerTitle: { fontSize: 32, fontWeight: 'bold', color: '#FFF' },
  profileSection: { alignItems: 'center', paddingHorizontal: 20, paddingBottom: 24 },
  avatarGradient: {
    width: 100,
    height: 100,
    borderRadius: 50,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
  },
  avatarText: { fontSize: 40, fontWeight: 'bold', color: '#FFF' },
  name: { fontSize: 24, fontWeight: 'bold', color: '#FFF', marginBottom: 4 },
  email: { fontSize: 14, color: '#9CA3AF' },
  stats: {
    flexDirection: 'row',
    marginTop: 24,
    backgroundColor: '#1F1F2E',
    borderRadius: 16,
    padding: 20,
    width: '100%',
  },
  statItem: { flex: 1, alignItems: 'center' },
  statValue: { fontSize: 20, fontWeight: 'bold', color: '#FFF' },
  statLabel: { fontSize: 12, color: '#9CA3AF', marginTop: 4 },
  statDivider: { width: 1, backgroundColor: '#2A2A3C' },
  section: { paddingHorizontal: 20, marginBottom: 20 },
  sectionTitle: { fontSize: 14, fontWeight: '600', color: '#6B7280', marginBottom: 12, textTransform: 'uppercase' },
  interestsContainer: { flexDirection: 'row', flexWrap: 'wrap' },
  interestChip: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    marginRight: 8,
    marginBottom: 8,
  },
  interestText: { fontSize: 14, fontWeight: '600' },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#1F1F2E',
    borderRadius: 12,
    padding: 16,
    marginBottom: 10,
  },
  menuItemLeft: { flexDirection: 'row', alignItems: 'center', flex: 1 },
  menuIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: '#8B5CF620',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  menuLabel: { fontSize: 16, color: '#FFF' },
  logoutButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#1F1F2E',
    borderRadius: 12,
    padding: 16,
    marginHorizontal: 20,
    marginBottom: 100,
    borderWidth: 1,
    borderColor: '#EF4444',
  },
  logoutText: { fontSize: 16, color: '#EF4444', fontWeight: '600', marginLeft: 8 },
});
