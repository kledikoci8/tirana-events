import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Image,
  TouchableOpacity,
  Dimensions,
} from 'react-native';
import api from '../services/api';
const { width } = Dimensions.get('window');
const ITEM_SIZE = (width - 48) / 2;

export default function MemoryWallScreen({ route }) {
  const { eventId } = route.params;
  const [memories, setMemories] = useState([]);

  useEffect(() => {
    loadMemories();
  }, []);

  const loadMemories = async () => {
    try {
      const response = await api.get(`/memories/events/${eventId}`);
      setMemories(response.data);
    } catch (error) {
      console.error('Error loading memories:', error);
    }
  };

  const likeMemory = async (memoryId) => {
    try {
      await api.post(`/memories/${memoryId}/like`);
      loadMemories();
    } catch (error) {
      console.error('Error liking memory:', error);
    }
  };

  const renderMemory = ({ item }) => (
    <View style={styles.memoryCard}>
      <Image source={{ uri: item.photoUrl }} style={styles.memoryImage} />
      
      <View style={styles.memoryOverlay}>
        <View style={styles.userInfo}>
          <Image
            source={{ uri: item.userAvatar || 'https://via.placeholder.com/24' }}
            style={styles.userAvatar}
          />
          <Text style={styles.userName}>{item.userName}</Text>
        </View>

        <TouchableOpacity
          style={styles.likeButton}
          onPress={() => likeMemory(item.id)}
        >
          <Text style={styles.likeIcon}>❤️</Text>
          <Text style={styles.likeCount}>{item.likes}</Text>
        </TouchableOpacity>
      </View>

      {item.caption && (
        <View style={styles.captionContainer}>
          <Text style={styles.caption} numberOfLines={2}>
            {item.caption}
          </Text>
        </View>
      )}
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Memory Wall</Text>
        <Text style={styles.subtitle}>Community photos from this event</Text>
      </View>

      <FlatList
        data={memories}
        renderItem={renderMemory}
        keyExtractor={(item) => item.id.toString()}
        numColumns={2}
        contentContainerStyle={styles.grid}
        columnWrapperStyle={styles.row}
        ListEmptyComponent={
          <View style={styles.emptyState}>
            <Text style={styles.emptyText}>No memories yet</Text>
            <Text style={styles.emptySubtext}>
              Upload photos within 48h after the event!
            </Text>
          </View>
        }
      />

      <TouchableOpacity style={styles.uploadButton}>
        <Text style={styles.uploadButtonText}>+ Upload Photo</Text>
      </TouchableOpacity>
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
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  grid: {
    padding: 16,
  },
  row: {
    justifyContent: 'space-between',
  },
  memoryCard: {
    width: ITEM_SIZE,
    marginBottom: 16,
    borderRadius: 12,
    overflow: 'hidden',
    backgroundColor: '#1A1A24',
  },
  memoryImage: {
    width: '100%',
    height: ITEM_SIZE,
  },
  memoryOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    padding: 8,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.6)',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  userAvatar: {
    width: 20,
    height: 20,
    borderRadius: 10,
    marginRight: 6,
  },
  userName: {
    fontSize: 12,
    color: '#FFFFFF',
    fontWeight: 'bold',
  },
  likeButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.6)',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  likeIcon: {
    fontSize: 16,
    marginRight: 4,
  },
  likeCount: {
    fontSize: 12,
    color: '#FFFFFF',
    fontWeight: 'bold',
  },
  captionContainer: {
    padding: 8,
  },
  caption: {
    fontSize: 12,
    color: '#FFFFFF',
    lineHeight: 16,
  },
  uploadButton: {
    position: 'absolute',
    bottom: 20,
    right: 20,
    backgroundColor: '#7F77DD',
    paddingHorizontal: 24,
    paddingVertical: 16,
    borderRadius: 30,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 8,
  },
  uploadButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
  emptyState: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 60,
    width: width - 32,
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
    textAlign: 'center',
  },
});
