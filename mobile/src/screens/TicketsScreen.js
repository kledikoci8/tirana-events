import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
  FlatList,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import QRCode from 'react-native-qrcode-svg';
import { Ionicons } from '@expo/vector-icons';
import api from '../services/api';

export default function TicketsScreen() {
  const [tickets, setTickets] = useState([]);
  const [selectedTab, setSelectedTab] = useState('upcoming');

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    try {
      const response = await api.get('/tickets/my-tickets');
      setTickets(response.data);
    } catch (error) {
      console.error('Error loading tickets:', error);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'short' });
    return `${day} ${month}`;
  };

  const renderTicket = ({ item }) => (
    <View style={styles.ticketCard}>
      <LinearGradient
        colors={['#1F1F2E', '#2A2A3C']}
        style={styles.ticketGradient}
      >
        <View style={styles.ticketHeader}>
          <View style={styles.ticketDate}>
            <Text style={styles.ticketDay}>
              {new Date(item.event.startDate).getDate()}
            </Text>
            <Text style={styles.ticketMonth}>
              {new Date(item.event.startDate).toLocaleString('default', { month: 'short' }).toUpperCase()}
            </Text>
          </View>
          <View style={styles.ticketInfo}>
            <Text style={styles.ticketTitle} numberOfLines={2}>
              {item.event.name}
            </Text>
            <View style={styles.ticketMeta}>
              <Ionicons name="location-outline" size={14} color="#9CA3AF" />
              <Text style={styles.ticketLocation} numberOfLines={1}>
                {item.event.location}
              </Text>
            </View>
          </View>
        </View>

        <View style={styles.divider}>
          <View style={styles.dividerCircleLeft} />
          <View style={styles.dividerLine} />
          <View style={styles.dividerCircleRight} />
        </View>

        <View style={styles.ticketQR}>
          <View style={styles.qrContainer}>
            <QRCode
              value={item.qrCode}
              size={120}
              backgroundColor="transparent"
              color="#FFFFFF"
            />
          </View>
          <Text style={styles.qrLabel}>Show this QR code at the entrance</Text>
          <Text style={styles.ticketId}>Ticket #{item.id}</Text>
        </View>
      </LinearGradient>
    </View>
  );

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <LinearGradient colors={['#0A0A0F', '#1A1A24']} style={styles.gradient}>
        <View style={styles.header}>
          <Text style={styles.title}>My Tickets</Text>
        </View>

        <View style={styles.tabs}>
          <TouchableOpacity
            style={[styles.tab, selectedTab === 'upcoming' && styles.tabActive]}
            onPress={() => setSelectedTab('upcoming')}
          >
            <Text
              style={[styles.tabText, selectedTab === 'upcoming' && styles.tabTextActive]}
            >
              Upcoming
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.tab, selectedTab === 'past' && styles.tabActive]}
            onPress={() => setSelectedTab('past')}
          >
            <Text
              style={[styles.tabText, selectedTab === 'past' && styles.tabTextActive]}
            >
              Past
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.tab, selectedTab === 'cancelled' && styles.tabActive]}
            onPress={() => setSelectedTab('cancelled')}
          >
            <Text
              style={[styles.tabText, selectedTab === 'cancelled' && styles.tabTextActive]}
            >
              Cancelled
            </Text>
          </TouchableOpacity>
        </View>

        {tickets.length === 0 ? (
          <View style={styles.emptyState}>
            <Ionicons name="ticket-outline" size={64} color="#6B7280" />
            <Text style={styles.emptyTitle}>No tickets yet</Text>
            <Text style={styles.emptyText}>
              Get tickets to your favorite events and they'll appear here
            </Text>
          </View>
        ) : (
          <FlatList
            data={tickets}
            renderItem={renderTicket}
            keyExtractor={(item) => item.id.toString()}
            contentContainerStyle={styles.ticketsList}
            showsVerticalScrollIndicator={false}
          />
        )}
      </LinearGradient>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  gradient: {
    flex: 1,
  },
  header: {
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 24,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  tabs: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    marginBottom: 24,
  },
  tab: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
    borderBottomWidth: 2,
    borderBottomColor: '#2A2A3C',
  },
  tabActive: {
    borderBottomColor: '#8B5CF6',
  },
  tabText: {
    fontSize: 14,
    color: '#6B7280',
    fontWeight: '600',
  },
  tabTextActive: {
    color: '#8B5CF6',
  },
  ticketsList: {
    paddingHorizontal: 20,
    paddingBottom: 100,
  },
  ticketCard: {
    marginBottom: 20,
    borderRadius: 16,
    overflow: 'hidden',
  },
  ticketGradient: {
    padding: 20,
  },
  ticketHeader: {
    flexDirection: 'row',
    marginBottom: 20,
  },
  ticketDate: {
    backgroundColor: '#8B5CF6',
    borderRadius: 12,
    padding: 12,
    alignItems: 'center',
    marginRight: 16,
    minWidth: 60,
  },
  ticketDay: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  ticketMonth: {
    fontSize: 12,
    color: '#FFFFFF',
    marginTop: -4,
  },
  ticketInfo: {
    flex: 1,
    justifyContent: 'center',
  },
  ticketTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  ticketMeta: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ticketLocation: {
    fontSize: 14,
    color: '#9CA3AF',
    marginLeft: 4,
    flex: 1,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 20,
  },
  dividerCircleLeft: {
    width: 20,
    height: 20,
    borderRadius: 10,
    backgroundColor: '#1A1A24',
    marginLeft: -30,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    borderTopWidth: 1,
    borderTopColor: '#2A2A3C',
    borderStyle: 'dashed',
  },
  dividerCircleRight: {
    width: 20,
    height: 20,
    borderRadius: 10,
    backgroundColor: '#1A1A24',
    marginRight: -30,
  },
  ticketQR: {
    alignItems: 'center',
  },
  qrContainer: {
    padding: 16,
    backgroundColor: '#FFFFFF',
    borderRadius: 12,
  },
  qrLabel: {
    fontSize: 12,
    color: '#9CA3AF',
    marginTop: 16,
  },
  ticketId: {
    fontSize: 10,
    color: '#6B7280',
    marginTop: 4,
  },
  emptyState: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 40,
  },
  emptyTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginTop: 16,
  },
  emptyText: {
    fontSize: 14,
    color: '#9CA3AF',
    textAlign: 'center',
    marginTop: 8,
  },
});
