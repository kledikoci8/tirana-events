import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  RefreshControl,
  StatusBar,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const FriendsScreen = ({ navigation }) => {
  const [activeTab, setActiveTab] = useState('friends'); // 'friends' or 'activity'
  const [friends, setFriends] = useState([]);
  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const token = await AsyncStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      if (activeTab === 'friends') {
        const response = await axios.get('http://localhost:8080/api/social/friends', { headers });
        setFriends(response.data);
      } else {
        const response = await axios.get('http://localhost:8080/api/social/activity/feed?limit=50', { headers });
        setActivities(response.data);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const onRefresh = () => {
    setRefreshing(true);
    fetchData();
  };

  const unfollowFriend = async (userId) => {
    try {
      const token = await AsyncStorage.getItem('token');
      await axios.delete(`http://localhost:8080/api/social/friends/${userId}/unfollow`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchData();
    } catch (error) {
      console.error('Error unfollowing:', error);
    }
  };

  const getActivityText = (activity) => {
    switch (activity.activityType) {
      case 'PURCHASED_TICKET':
        return 'bought a ticket to';
      case 'SAVED_EVENT':
        return 'saved';
      case 'ATTENDING':
        return 'is attending';
      case 'INTERESTED':
        return 'is interested in';
      default:
        return 'interacted with';
    }
  };

  const getTimeAgo = (timestamp) => {
    const now = new Date();
    const time = new Date(timestamp);
    const diff = Math.floor((now - time) / 1000); // seconds

    if (diff < 60) return 'Just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}d ago`;
    return `${Math.floor(diff / 604800)}w ago`;
  };

  const renderFriendItem = ({ item }) => (
    <View style={styles.friendCard}>
      <View style={styles.friendInfo}>
        <View style={styles.avatar}>
          {item.profileImage ? (
            <Image source={{ uri: item.profileImage }} style={styles.avatarImage} />
          ) : (
            <View style={styles.avatarPlaceholder}>
              <Text style={styles.avatarText}>
                {item.fullName.charAt(0).toUpperCase()}
              </Text>
            </View>
          )}
        </View>
        <View style={styles.friendDetails}>
          <Text style={styles.friendName}>{item.fullName}</Text>
          <Text style={styles.friendEmail}>{item.email}</Text>
        </View>
      </View>
      <View style={styles.friendActions}>
        <TouchableOpacity
          style={styles.unfollowButton}
          onPress={() => unfollowFriend(item.id)}
        >
          <Ionicons name="person-remove" size={20} color="#FF6B6B" />
        </TouchableOpacity>
      </View>
    </View>
  );

  const renderActivityItem = ({ item }) => (
    <TouchableOpacity
      style={styles.activityCard}
      onPress={() => navigation.navigate('EventDetail', { eventId: item.eventId })}
      activeOpacity={0.7}
    >
      <View style={styles.activityHeader}>
        <View style={styles.avatar}>
          {item.user.profileImage ? (
            <Image source={{ uri: item.user.profileImage }} style={styles.avatarImage} />
          ) : (
            <View style={styles.avatarPlaceholder}>
              <Text style={styles.avatarText}>
                {item.user.fullName.charAt(0).toUpperCase()}
              </Text>
            </View>
          )}
        </View>
        <View style={styles.activityInfo}>
          <Text style={styles.activityText}>
            <Text style={styles.activityUserName}>{item.user.fullName}</Text>
            {' '}{getActivityText(item)}{' '}
            <Text style={styles.activityEventName}>{item.eventName}</Text>
          </Text>
          <Text style={styles.activityTime}>{getTimeAgo(item.timestamp)}</Text>
        </View>
      </View>
      {item.eventImageUrl && (
        <Image source={{ uri: item.eventImageUrl }} style={styles.activityImage} />
      )}
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Friends</Text>
        <TouchableOpacity onPress={() => navigation.navigate('SearchUsers')}>
          <Ionicons name="person-add" size={24} color="#7F77DD" />
        </TouchableOpacity>
      </View>

      {/* Tabs */}
      <View style={styles.tabs}>
        <TouchableOpacity
          style={[styles.tab, activeTab === 'friends' && styles.tabActive]}
          onPress={() => setActiveTab('friends')}
        >
          <Text style={[styles.tabText, activeTab === 'friends' && styles.tabTextActive]}>
            Friends
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.tab, activeTab === 'activity' && styles.tabActive]}
          onPress={() => setActiveTab('activity')}
        >
          <Text style={[styles.tabText, activeTab === 'activity' && styles.tabTextActive]}>
            Activity
          </Text>
        </TouchableOpacity>
      </View>

      {/* Content */}
      <FlatList
        data={activeTab === 'friends' ? friends : activities}
        renderItem={activeTab === 'friends' ? renderFriendItem : renderActivityItem}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.listContent}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor="#7F77DD"
          />
        }
        ListEmptyComponent={
          <View style={styles.emptyState}>
            <Ionicons
              name={activeTab === 'friends' ? 'people-outline' : 'notifications-outline'}
              size={64}
              color="#3A3A4A"
            />
            <Text style={styles.emptyText}>
              {activeTab === 'friends'
                ? 'No friends yet. Start following people!'
                : 'No recent activity from friends'}
            </Text>
          </View>
        }
      />
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
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  tabs: {
    flexDirection: 'row',
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  tab: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
    borderBottomWidth: 2,
    borderBottomColor: 'transparent',
  },
  tabActive: {
    borderBottomColor: '#7F77DD',
  },
  tabText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#A0A0B0',
  },
  tabTextActive: {
    color: '#7F77DD',
  },
  listContent: {
    padding: 20,
  },
  friendCard: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  friendInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  avatar: {
    width: 48,
    height: 48,
    borderRadius: 24,
    marginRight: 12,
  },
  avatarImage: {
    width: '100%',
    height: '100%',
    borderRadius: 24,
  },
  avatarPlaceholder: {
    width: '100%',
    height: '100%',
    borderRadius: 24,
    backgroundColor: '#7F77DD',
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  friendDetails: {
    flex: 1,
  },
  friendName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  friendEmail: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  friendActions: {
    flexDirection: 'row',
  },
  unfollowButton: {
    padding: 8,
  },
  activityCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  activityHeader: {
    flexDirection: 'row',
    marginBottom: 12,
  },
  activityInfo: {
    flex: 1,
  },
  activityText: {
    fontSize: 14,
    color: '#FFFFFF',
    lineHeight: 20,
    marginBottom: 4,
  },
  activityUserName: {
    fontWeight: 'bold',
    color: '#7F77DD',
  },
  activityEventName: {
    fontWeight: '600',
    color: '#FFFFFF',
  },
  activityTime: {
    fontSize: 12,
    color: '#A0A0B0',
  },
  activityImage: {
    width: '100%',
    height: 150,
    borderRadius: 8,
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 60,
  },
  emptyText: {
    fontSize: 16,
    color: '#A0A0B0',
    marginTop: 16,
    textAlign: 'center',
  },
});

export default FriendsScreen;
