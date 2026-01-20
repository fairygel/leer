import { Modal, Pressable, SectionList, Text, TextInput, View } from 'react-native';
import { storage } from '@/services/storage';
import { router } from 'expo-router';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { Colors } from '@/constants/Colors';
import { FONT_SIZES } from '@/constants/Sizes';
import Ionicons from '@expo/vector-icons/Ionicons';
import { useEffect, useState } from 'react';
import { CardSet } from '@/types';
import { SetService } from '@/services/SetService';
import { FontAwesome5 } from '@expo/vector-icons';
import { FONTS } from '@/constants/Fonts';
import { SetModal } from '@/components/SetModal';

async function fetchItems(): Promise<CardSet[]> {
	try {
		return await SetService.getAll();
	} catch (e) {
		console.error(e);
		return [];
	}
}

function groupByDate(items: CardSet[]) {
	const grouped: { [key: string]: CardSet[] } = {};
	const dateVales: { [key: string]: Date } = {};

	items.forEach((item) => {
		const date = new Date(item.updatedAt);

		const dateKey = new Intl.DateTimeFormat('default', {
			year: 'numeric',
			month: 'long',
			day: 'numeric',
		}).format(date);

		if (!grouped[dateKey]) {
			grouped[dateKey] = [];
		}
		grouped[dateKey].push(item);
		dateVales[dateKey] = date;
	});

	return Object.entries(grouped)
		.sort(([dateA], [dateB]) => {
			return dateVales[dateB].getTime() - dateVales[dateA].getTime();
		})
		.map(([title, data]) => ({
			title,
			data: data.sort((a, b) =>
				new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime(),
			),
		}));
}
export default function Index() {
	const [isModalVisible, setModalVisible] = useState(false);

	const [items, setItems] = useState<CardSet[]>([]);
	const insets = useSafeAreaInsets();

	const logout = async () => {
		await storage.removeToken();
		router.replace('/(auth)/login');
	};

	useEffect(() => {
		fetchItems().then((sets) => {
			setItems(sets);
		}).catch(console.error);
	}, []);

	const handleCreateSuccess = async (result: any) => {
		try {
			const sets = await SetService.getAll();
			setItems(sets);
			if (result && result.insertedId) {
				const newItem = sets.find(s => s._id === result.insertedId);
				if (newItem) {
					router.push({
						pathname: '/cardset/[id]',
						params: {
							id: result.insertedId,
							data: JSON.stringify(newItem),
						},
					});
				} else {
                    // Fallback if not found locally yet, although getAll should have it.
                    // Or just push without data, let [id] fetch it.
					router.push({
						pathname: '/cardset/[id]',
						params: { id: result.insertedId }
					});
                }
			}
		} catch (e) {
			console.error(e);
		}
	};

	const sections = groupByDate(items);

	return (
		<View style={{ flex: 1, paddingTop: insets.top, backgroundColor: Colors.secondary }}>
			<SetModal
				visible={isModalVisible}
				onClose={() => setModalVisible(false)}
				onSuccess={handleCreateSuccess}
			/>

			<View style={{
				paddingHorizontal: 24,
				flexDirection: 'row',
				alignItems: 'center',
				justifyContent: 'space-between',
			}}>
				<Text style={{
					fontSize: FONT_SIZES.heading,
					color: Colors.primary,
					fontFamily: FONTS.heading,
				}}>Card Sets</Text>
				<Pressable onPress={logout}>
					<Ionicons
						name="exit-outline"
						size={FONT_SIZES.heading}
						color={Colors.primary}
					/>
				</Pressable>
			</View>

			<SectionList sections={sections}
						 keyExtractor={(item) => item._id}
						 renderItem={({ item }) => (
							 <Pressable
								 onPress={() => router.push({
									 pathname: '/cardset/[id]',
									 params: {
										 id: item._id,
										 data: JSON.stringify(item),
									 },
								 } as const)}
								 style={{
									 borderWidth: 2,
									 borderColor: Colors.primary,
									 padding: 12,
									 borderRadius: 8,
									 marginHorizontal: 12,
									 marginVertical: 8,
									 flexDirection: 'row',
									 justifyContent: 'space-between',
								 }}
							 >
								 <Text style={{ color: Colors.primary, fontSize: FONT_SIZES.body }}>{item.name}</Text>
								 <View style={{ flexDirection: 'row', alignItems: 'center' }}>
									 {/*<Text style={{ color: '#c17272', fontSize: FONT_SIZES.small }}>13 left</Text>*/}
								 </View>
							 </Pressable>
						 )}
						 renderSectionHeader={({ section: { title } }) => (
							 <View style={{
								 paddingHorizontal: 24,
								 paddingVertical: 12,
								 alignItems: 'center',
							 }}>
								 <View style={{
									 position: 'absolute',
									 width: '100%',
									 height: 1,
									 backgroundColor: Colors.primary,
									 top: '100%',
								 }} />

								 <Text style={{
									 color: Colors.primary,
									 backgroundColor: Colors.secondary,
									 fontSize: FONT_SIZES.small,
									 paddingHorizontal: 8,
									 fontWeight: '600',
									 zIndex: 1,
								 }}>
									 {title}
								 </Text>
							 </View>
						 )}
			/>
			<Pressable style={{
				position: 'absolute',
				right: 0,
				bottom: 0,
				width: 64,
				height: 64,
				alignItems: 'center',
				justifyContent: 'center',
				marginBottom: insets.bottom + 24,
				marginRight: insets.right + 24,
				borderColor: Colors.primary,
				borderWidth: 2,
				borderRadius: 24,
				backgroundColor: Colors.secondary,
				zIndex: 10,
			}} onPress={() => {
				setModalVisible(true);
			}}>
				<FontAwesome5 name="plus" size={FONT_SIZES.heading2} color={Colors.primary} />
			</Pressable>
		</View>
	);
}