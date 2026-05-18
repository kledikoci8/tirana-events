import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  TextInput,
  Modal,
} from 'react-native';
import api from '../services/api';

const BOARD_TYPES = [
  { id: 'GENERAL', label: 'General', icon: '💬' },
  { id: 'LOOKING_FOR_BUDDY', label: 'Find a Buddy', icon: '👥' },
  { id: 'ORGANIZER_TEASER', label: 'Teasers', icon: '🎬' },
  { id: 'LINEUP_REVEAL', label: 'Lineups', icon: '🎵' },
];

export default function CommunityBoardScreen({ navigation }) {
  const [selectedBoard, setSelectedBoard] = useState('GENERAL');
  const [posts, setPosts] = useState([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newPost, setNewPost] = useState({ title: '', content: '' });

  useEffect(() => {
    loadPosts();
  }, [selectedBoard]);

  const loadPosts = async () => {
    try {
      const response = await api.get(`/community/boards/${selectedBoard}`);
      setPosts(response.data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const createPost = async () => {
    try {
      await api.post('/community/posts', {
        boardType: selectedBoard,
        title: newPost.title,
        content: newPost.content,
      });
      setShowCreateModal(false);
      setNewPost({ title: '', content: '' });
      loadPosts();
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const upvotePost = async (postId) => {
    try {
      await api.post(`/community/posts/${postId}/upvote`);
      loadPosts();
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const renderPost = ({ item }) => (
    <TouchableOpacity
      style={[styles.postCard, item.isPinned && styles.pinnedPost]}
      onPress={() => navigation.navigate('PostDetail', { postId: item.id })}
    >
      {item.isPinned && (
        <View style={styles.pinnedBadge}>
          <Text style={styles.pinnedText}>📌 Pinned</Text>
        </View>
      )}
      
      <View style={styles.postHeader}>
        <Image
          source={{ uri: item.userAvatar || 'https://via.placeholder.com/40' }}
          style={styles.userAvatar}
        />
        <View style={styles.postHeaderText}>
          <Text style={styles.userName}>{item.userName}</Text>
          <Text style={styles.postTime}>
            {new Date(item.createdAt).toLocaleDateString()}
          </Text>
        </View>
      </View>

      <Text style={styles.postTitle}>{item.title}</Text>
      <Text style={styles.postContent} numberOfLines={3}>
        {item.content}
      </Text>

      <View style={styles.postFooter}>
        <TouchableOpacity
          style={styles.upvoteButton}
          onPress={() => upvotePost(item.id)}
        >
          <Text style={styles.upvoteIcon}>👍</Text>
          <Text style={styles.upvoteCount}>{item.upvotes}</Text>
        </TouchableOpacity>
        <View style={styles.commentButton}>
          <Text style={styles.commentIcon}>💬</Text>
          <Text style={styles.commentCount}>{item.commentsCount}</Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Community</Text>
        <TouchableOpacity
          style={styles.createButton}
          onPress={() => setShowCreateModal(true)}
        >
          <Text style={styles.createButtonText}>+ New Post</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={BOARD_TYPES}
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.boardTabs}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={[styles.boardTab, selectedBoard === item.id && styles.boardTabActive]}
            onPress={() => setSelectedBoard(item.id)}
          >
            <Text style={styles.boardIcon}>{item.icon}</Text>
            <Text style={[styles.boardLabel, selectedBoard === item.id && styles.boardLabelActive]}>
              {item.label}
            </Text>
          </TouchableOpacity>
        )}
        keyExtractor={(item) => item.id}
      />

      <FlatList
        data={posts}
        renderItem={renderPost}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
      />

      <Modal visible={showCreateModal} animationType="slide" transparent>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Create Post</Text>
            
            <TextInput
              style={styles.input}
              placeholder="Title"
              placeholderTextColor="#A0A0B0"
              value={newPost.title}
              onChangeText={(text) => setNewPost({ ...newPost, title: text })}
            />
            
            <TextInput
              style={[styles.input, styles.textArea]}
              placeholder="What's on your mind?"
              placeholderTextColor="#A0A0B0"
              multiline
              value={newPost.content}
              onChangeText={(text) => setNewPost({ ...newPost, content: text })}
            />

            <View style={styles.modalButtons}>
              <TouchableOpacity
                style={styles.cancelButton}
                onPress={() => setShowCreateModal(false)}
              >
                <Text style={styles.cancelButtonText}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.postButton} onPress={createPost}>
                <Text style={styles.postButtonText}>Post</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
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
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
  },
  createButtonText: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: 'bold',
  },
  boardTabs: {
    paddingHorizontal: 20,
    paddingBottom: 16,
  },
  boardTab: {
    backgroundColor: '#1A1A24',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: 20,
    marginRight: 12,
    flexDirection: 'row',
    alignItems: 'center',
  },
  boardTabActive: {
    backgroundColor: '#7F77DD',
  },
  boardIcon: {
    fontSize: 16,
    marginRight: 6,
  },
  boardLabel: {
    fontSize: 14,
    color: '#A0A0B0',
    fontWeight: 'bold',
  },
  boardLabelActive: {
    color: '#FFFFFF',
  },
  list: {
    padding: 20,
  },
  postCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
  },
  pinnedPost: {
    borderWidth: 2,
    borderColor: '#7F77DD',
  },
  pinnedBadge: {
    marginBottom: 12,
  },
  pinnedText: {
    fontSize: 12,
    color: '#7F77DD',
    fontWeight: 'bold',
  },
  postHeader: {
    flexDirection: 'row',
    marginBottom: 12,
  },
  userAvatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 12,
  },
  postHeaderText: {
    flex: 1,
  },
  userName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 2,
  },
  postTime: {
    fontSize: 12,
    color: '#A0A0B0',
  },
  postTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  postContent: {
    fontSize: 14,
    color: '#A0A0B0',
    lineHeight: 20,
    marginBottom: 12,
  },
  postFooter: {
    flexDirection: 'row',
    gap: 16,
  },
  upvoteButton: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  upvoteIcon: {
    fontSize: 16,
    marginRight: 6,
  },
  upvoteCount: {
    fontSize: 14,
    color: '#FFFFFF',
    fontWeight: 'bold',
  },
  commentButton: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  commentIcon: {
    fontSize: 16,
    marginRight: 6,
  },
  commentCount: {
    fontSize: 14,
    color: '#A0A0B0',
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.8)',
    justifyContent: 'flex-end',
  },
  modalContent: {
    backgroundColor: '#1A1A24',
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    padding: 24,
  },
  modalTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 20,
  },
  input: {
    backgroundColor: '#0A0A0F',
    color: '#FFFFFF',
    padding: 16,
    borderRadius: 12,
    fontSize: 16,
    marginBottom: 16,
  },
  textArea: {
    minHeight: 120,
    textAlignVertical: 'top',
  },
  modalButtons: {
    flexDirection: 'row',
    gap: 12,
  },
  cancelButton: {
    flex: 1,
    padding: 16,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#7F77DD',
    alignItems: 'center',
  },
  cancelButtonText: {
    color: '#7F77DD',
    fontSize: 16,
    fontWeight: 'bold',
  },
  postButton: {
    flex: 1,
    backgroundColor: '#7F77DD',
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  postButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
