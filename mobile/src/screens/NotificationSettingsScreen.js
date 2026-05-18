import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Switch,
  TouchableOpacity,
  StatusBar,
  Alert,
  Platform,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';
import * as Notifications from 'expo-notifications';

const NotificationSettingsScreen = ({ navigation }) => {
  const [notifyEventReminder, setNotifyEventReminder] = useState(true);
  const [notifyPriceDrop, setNotifyPriceDrop] = useState(true);
  const [notifyFriendActivity, setNotifyFriendActivity] = useState(true);
  const [notifyNearbyEvents, setNotifyNearbyEvents] = useState(true);
  const [quietHoursStart, setQuietHoursStart] = useState(23);
  const [quietHoursEnd, setQuietHoursEnd] = useState(8);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchPreferences();
    requestNotificationPermissions();
  }, []);

  const requestNotificationPermissions = async () => {
    const { status: existingStatus } = await Notifications.getPermissionsAsync();
    let finalStatus = existingStatus;
    
    if (existingStatus !== 'granted') {
      const { status } = await Notifications.requestPermissionsAsync();
      finalStatus = status;
    }
    
    if (finalStatus !== 'granted') {
      Alert.alert(
        'Notifications Disabled',
        'Enable notifications for local ticket reminders. In-app alerts are always stored in your inbox.',
        [{ text: 'OK' }]
      );
    }
  };

  const fetchPreferences = async () => {
    try {
      const response = await api.get('/notifications/preferences');
      
      const prefs = response.data;
      setNotifyEventReminder(prefs.notifyEventReminder);
      setNotifyPriceDrop(prefs.notifyPriceDrop);
      setNotifyFriendActivity(prefs.notifyFriendActivity);
      setNotifyNearbyEvents(prefs.notifyNearbyEvents);
      setQuietHoursStart(prefs.quietHoursStart);
      setQuietHoursEnd(prefs.quietHoursEnd);
    } catch (error) {
      console.error('Error fetching preferences:', error);
    }
  };

  const savePreferences = async () => {
    setLoading(true);
    try {
      await api.put('/notifications/preferences', {
        notifyEventReminder,
        notifyPriceDrop,
        notifyFriendActivity,
        notifyNearbyEvents,
        quietHoursStart,
        quietHoursEnd,
      });
      
      Alert.alert('Success', 'Notification preferences saved!');
    } catch (error) {
      console.error('Error saving preferences:', error);
      Alert.alert('Error', 'Failed to save preferences. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const adjustQuietHours = (type, increment) => {
    if (type === 'start') {
      let newHour = quietHoursStart + increment;
      if (newHour < 0) newHour = 23;
      if (newHour > 23) newHour = 0;
      setQuietHoursStart(newHour);
    } else {
      let newHour = quietHoursEnd + increment;
      if (newHour < 0) newHour = 23;
      if (newHour > 23) newHour = 0;
      setQuietHoursEnd(newHour);
    }
  };

  const formatHour = (hour) => {
    return `${hour.toString().padStart(2, '0')}:00`;
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Notifications</Text>
        <View style={{ width: 24 }} />
      </View>

      <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
        <View style={styles.infoBanner}>
          <Ionicons name="information-circle" size={20} color="#8B5CF6" />
          <Text style={styles.infoBannerText}>
            Alerts are saved in your in-app inbox (MySQL). Local reminders fire 24h and 2h before events — no Firebase push.
          </Text>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Notification Types</Text>
          
          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Ionicons name="calendar" size={24} color="#7F77DD" />
              <View style={styles.settingText}>
                <Text style={styles.settingLabel}>Event Reminders</Text>
                <Text style={styles.settingDescription}>
                  Get notified 24h and 2h before events
                </Text>
              </View>
            </View>
            <Switch
              value={notifyEventReminder}
              onValueChange={setNotifyEventReminder}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>

          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Ionicons name="pricetag" size={24} color="#7F77DD" />
              <View style={styles.settingText}>
                <Text style={styles.settingLabel}>Price Drops</Text>
                <Text style={styles.settingDescription}>
                  When saved events reduce their price
                </Text>
              </View>
            </View>
            <Switch
              value={notifyPriceDrop}
              onValueChange={setNotifyPriceDrop}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>

          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Ionicons name="people" size={24} color="#7F77DD" />
              <View style={styles.settingText}>
                <Text style={styles.settingLabel}>Friend Activity</Text>
                <Text style={styles.settingDescription}>
                  When friends buy tickets or save events
                </Text>
              </View>
            </View>
            <Switch
              value={notifyFriendActivity}
              onValueChange={setNotifyFriendActivity}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>

          <View style={styles.settingRow}>
            <View style={styles.settingInfo}>
              <Ionicons name="location" size={24} color="#7F77DD" />
              <View style={styles.settingText}>
                <Text style={styles.settingLabel}>Nearby Events</Text>
                <Text style={styles.settingDescription}>
                  Events happening within 500m of you
                </Text>
              </View>
            </View>
            <Switch
              value={notifyNearbyEvents}
              onValueChange={setNotifyNearbyEvents}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>
        </View>

        {/* Quiet Hours */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Quiet Hours</Text>
          <Text style={styles.sectionDescription}>
            No notifications will be sent during these hours
          </Text>

          <View style={styles.quietHoursContainer}>
            <View style={styles.quietHourControl}>
              <Text style={styles.quietHourLabel}>Start</Text>
              <View style={styles.timeControl}>
                <TouchableOpacity
                  style={styles.timeButton}
                  onPress={() => adjustQuietHours('start', -1)}
                >
                  <Ionicons name="remove" size={20} color="#FFFFFF" />
                </TouchableOpacity>
                <Text style={styles.timeText}>{formatHour(quietHoursStart)}</Text>
                <TouchableOpacity
                  style={styles.timeButton}
                  onPress={() => adjustQuietHours('start', 1)}
                >
                  <Ionicons name="add" size={20} color="#FFFFFF" />
                </TouchableOpacity>
              </View>
            </View>

            <View style={styles.quietHourControl}>
              <Text style={styles.quietHourLabel}>End</Text>
              <View style={styles.timeControl}>
                <TouchableOpacity
                  style={styles.timeButton}
                  onPress={() => adjustQuietHours('end', -1)}
                >
                  <Ionicons name="remove" size={20} color="#FFFFFF" />
                </TouchableOpacity>
                <Text style={styles.timeText}>{formatHour(quietHoursEnd)}</Text>
                <TouchableOpacity
                  style={styles.timeButton}
                  onPress={() => adjustQuietHours('end', 1)}
                >
                  <Ionicons name="add" size={20} color="#FFFFFF" />
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </View>

        {/* Info Box */}
        <View style={styles.infoBox}>
          <Ionicons name="information-circle" size={24} color="#7F77DD" />
          <Text style={styles.infoText}>
            You can manage notification permissions in your device settings
          </Text>
        </View>
      </ScrollView>

      {/* Save Button */}
      <View style={styles.footer}>
        <TouchableOpacity
          style={styles.saveButton}
          onPress={savePreferences}
          disabled={loading}
        >
          <Text style={styles.saveButtonText}>
            {loading ? 'Saving...' : 'Save Preferences'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
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
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  content: {
    flex: 1,
  },
  section: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#1A1A24',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  sectionDescription: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 20,
  },
  settingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 16,
  },
  settingInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  settingText: {
    marginLeft: 16,
    flex: 1,
  },
  settingLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  settingDescription: {
    fontSize: 13,
    color: '#A0A0B0',
  },
  quietHoursContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  quietHourControl: {
    flex: 1,
    marginHorizontal: 8,
  },
  quietHourLabel: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 12,
    textAlign: 'center',
  },
  timeControl: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 12,
  },
  timeButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#7F77DD',
    justifyContent: 'center',
    alignItems: 'center',
  },
  timeText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  infoBanner: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginHorizontal: 20,
    marginTop: 12,
    marginBottom: 8,
    padding: 14,
    backgroundColor: 'rgba(139,92,246,0.12)',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: 'rgba(139,92,246,0.25)',
  },
  infoBannerText: {
    flex: 1,
    marginLeft: 10,
    fontSize: 13,
    color: '#C4B5FD',
    lineHeight: 18,
  },
  infoBox: {
    flexDirection: 'row',
    alignItems: 'center',
    margin: 20,
    padding: 16,
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    borderLeftWidth: 4,
    borderLeftColor: '#7F77DD',
  },
  infoText: {
    flex: 1,
    fontSize: 14,
    color: '#A0A0B0',
    marginLeft: 12,
    lineHeight: 20,
  },
  footer: {
    padding: 20,
    borderTopWidth: 1,
    borderTopColor: '#1A1A24',
  },
  saveButton: {
    backgroundColor: '#7F77DD',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
  },
  saveButtonText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
});

export default NotificationSettingsScreen;
