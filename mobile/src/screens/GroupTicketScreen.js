import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  Modal,
  TextInput,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_URL = 'http://192.168.1.6:8080/api';

export default function GroupTicketScreen({ navigation }) {
  const [groupTickets, setGroupTickets] = useState([]);
  const [showCreateModal, setShowCreateModal] = useState(false);

  useEffect(() => {
    loadGroupTickets();
  }, []);

  const loadGroupTickets = async () => {
    try {
      const token = await AsyncStorage.getItem('token');
      const response = await fetch(`${API_URL}/group-tickets/my-groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await response.json();
      setGroupTickets(data);
    } catch (error) {
      console.error('Error loading group tickets:', error);
    }
  };

  const payForTicket = async (participantId) => {
    try {
      const token = await AsyncStorage.getItem('token');
      await fetch(`${API_URL}/group-tickets/participants/${participantId}/pay`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });
      loadGroupTickets();
    } catch (error) {
      console.error('Error paying:', error);
    }
  };

  const renderParticipant = (participant) => (
    <View key={participant.id} style={styles.participantRow}>
      <Image
        source={{ uri: participant.userAvatar || 'https://via.placeholder.com/32' }}
        style={styles.participantAvatar}
      />
      <Text style={styles.participantName}>{participant.userName}</Text>
      <View style={[styles.statusBadge, { backgroundColor: getStatusColor(participant.paymentStatus) }]}>
        <Text style={styles.statusText}>{participant.paymentStatus}</Text>
      </View>
    </View>
  );

  const renderGroupTicket = ({ item }) => (
    <View style={styles.groupCard}>
      <Image source={{ uri: item.eventImageUrl }} style={styles.eventImage} />
      
      <View style={styles.groupInfo}>
        <Text style={styles.eventName}>{item.eventName}</Text>
        <Text style={styles.organizerText}>Organized by {item.organizerName}</Text>

        <View style={styles.priceRow}>
          <Text style={styles.totalPrice}>{item.totalPrice} ALL total</Text>
          <Text style={styles.perPersonPrice}>{item.pricePerPerson} ALL per person</Text>
        </View>

        <View style={styles.progressRow}>
          <Text style={styles.progressText}>
            {item.paidCount}/{item.totalTickets} paid
          </Text>
          <View style={styles.progressBar}>
            <View
              style={[styles.progressFill, { width: `${(item.paidCount / item.totalTickets) * 100}%` }]}
            />
          </View>
        </View>

        <Text style={styles.participantsTitle}>Participants:</Text>
        {item.participants.map(renderParticipant)}

        {item.status === 'PENDING' && (
          <View style={styles.expiryWarning}>
            <Text style={styles.expiryText}>
              Expires: {new Date(item.expiresAt).toLocaleString()}
            </Text>
          </View>
        )}

        {item.participants.some(p => p.paymentStatus === 'PENDING') && (
          <TouchableOpacity
            style={styles.payButton}
            onPress={() => {
              const myParticipant = item.participants.find(p => p.paymentStatus === 'PENDING');
              if (myParticipant) payForTicket(myParticipant.id);
            }}
          >
            <Text style={styles.payButtonText}>Pay My Share</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );

  const getStatusColor = (status) => {
    switch (status) {
      case 'PAID': return '#4CAF50';
      case 'PENDING': return '#FF9800';
      case 'REFUNDED': return '#F44336';
      default: return '#A0A0B0';
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Group Tickets</Text>
        <TouchableOpacity
          style={styles.createButton}
          onPress={() => setShowCreateModal(true)}
        >
          <Text style={styles.createButtonText}>+ Create Group</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={groupTickets}
        renderItem={renderGroupTicket}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
        ListEmptyComponent={
          <View style={styles.emptyState}>
            <Text style={styles.emptyText}>No group tickets yet</Text>
            <Text style={styles.emptySubtext}>Split ticket costs with friends!</Text>
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
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  createButton: {
    backgroundColor: '#7F77DD',
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
  },
  createButtonText: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  list: {
    padding: 16,
  },
  groupCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    marginBottom: 16,
    overflow: 'hidden',
  },
  eventImage: {
    width: '100%',
    height: 150,
  },
  groupInfo: {
    padding: 16,
  },
  eventName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  organizerText: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 12,
  },
  priceRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  totalPrice: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#7F77DD',
  },
  perPersonPrice: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  progressRow: {
    marginBottom: 16,
  },
  progressText: {
    fontSize: 14,
    color: '#FFFFFF',
    marginBottom: 8,
  },
  progressBar: {
    height: 6,
    backgroundColor: '#0A0A0F',
    borderRadius: 3,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#4CAF50',
  },
  participantsTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 12,
  },
  participantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  participantAvatar: {
    width: 32,
    height: 32,
    borderRadius: 16,
    marginRight: 12,
  },
  participantName: {
    flex: 1,
    fontSize: 14,
    color: '#FFFFFF',
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statusText: {
    color: '#FFFFFF',
    fontSize: 12,
    fontWeight: 'bold',
  },
  expiryWarning: {
    backgroundColor: '#FF9800',
    padding: 12,
    borderRadius: 8,
    marginTop: 12,
  },
  expiryText: {
    color: '#FFFFFF',
    fontSize: 14,
    textAlign: 'center',
  },
  payButton: {
    backgroundColor: '#7F77DD',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
    marginTop: 12,
  },
  payButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
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
