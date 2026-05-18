import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
} from 'react-native';

import api from '../services/api';

export default function CuratorListsScreen({ navigation }) {
  const [curators, setCurators] = useState([]);
  const [trendingLists, setTrendingLists] = useState([]);

  useEffect(() => {
    loadCurators();
    loadTrendingLists();
  }, []);

  const loadCurators = async () => {
    try {
      const response = await api.get('/curators');
      setCurators(response.data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const loadTrendingLists = async () => {
    try {
      const response = await api.get('/curators/lists/trending');
      setTrendingLists(response.data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const renderCurator = ({ item }) => (
    <TouchableOpacity
      style={styles.curatorCard}
      onPress={() => navigation.navigate('CuratorDetail', { curatorId: item.id })}
    >
      <Image
        source={{ uri: item.avatarUrl || 'https://via.placeholder.com/60' }}
        style={styles.curatorAvatar}
      />
      <View style={styles.curatorInfo}>
        <View style={styles.curatorNameRow}>
          <Text style={styles.curatorName}>{item.displayName}</Text>
          {item.isVerified && <Text style={styles.verifiedBadge}>✓</Text>}
        </View>
        <Text style={styles.curatorBio} numberOfLines={2}>
          {item.bio}
        </Text>
        <Text style={styles.curatorStats}>
          {item.followersCount} followers • {item.curatedListsCount} lists
        </Text>
      </View>
    </TouchableOpacity>
  );

  const renderList = ({ item }) => (
    <TouchableOpacity
      style={styles.listCard}
      onPress={() => navigation.navigate('CuratedListDetail', { listId: item.id })}
    >
      <Image source={{ uri: item.coverImageUrl }} style={styles.listImage} />
      <View style={styles.listInfo}>
        <Text style={styles.listTitle}>{item.title}</Text>
        <Text style={styles.listCurator}>by {item.curator.displayName}</Text>
        <Text style={styles.listStats}>
          {item.events.length} events • {item.viewsCount} views
        </Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Curated Lists</Text>
        <Text style={styles.subtitle}>Handpicked events by local experts</Text>
      </View>

      <Text style={styles.sectionTitle}>Featured Curators</Text>
      <FlatList
        data={curators}
        renderItem={renderCurator}
        keyExtractor={(item) => item.id.toString()}
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.horizontalList}
      />

      <Text style={styles.sectionTitle}>Trending Lists</Text>
      <FlatList
        data={trendingLists}
        renderItem={renderList}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
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
    padding: 20,
    paddingTop: 60,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 16,
    color: '#A0A0B0',
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
    paddingHorizontal: 20,
    marginTop: 20,
    marginBottom: 12,
  },
  horizontalList: {
    paddingHorizontal: 20,
  },
  curatorCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    padding: 16,
    marginRight: 16,
    width: 280,
    flexDirection: 'row',
  },
  curatorAvatar: {
    width: 60,
    height: 60,
    borderRadius: 30,
    marginRight: 12,
  },
  curatorInfo: {
    flex: 1,
  },
  curatorNameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  curatorName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginRight: 4,
  },
  verifiedBadge: {
    fontSize: 16,
    color: '#7F77DD',
  },
  curatorBio: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 8,
  },
  curatorStats: {
    fontSize: 12,
    color: '#7F77DD',
  },
  list: {
    padding: 20,
  },
  listCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    marginBottom: 16,
    overflow: 'hidden',
  },
  listImage: {
    width: '100%',
    height: 150,
  },
  listInfo: {
    padding: 16,
  },
  listTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  listCurator: {
    fontSize: 14,
    color: '#7F77DD',
    marginBottom: 8,
  },
  listStats: {
    fontSize: 12,
    color: '#A0A0B0',
  },
});
