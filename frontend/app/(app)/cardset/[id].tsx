import { useLocalSearchParams } from 'expo-router';
import { View, Text, Pressable, TextInput, ScrollView } from 'react-native';
import { useRouter } from 'expo-router';
import Ionicons from '@expo/vector-icons/Ionicons';
import { Colors } from '@/constants/Colors';
import { FONT_SIZES } from '@/constants/Sizes';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useEffect, useState } from 'react';
import { CardSet, FlashCard } from '@/types';
import { SetService } from '@/services/SetService';
import { CardService } from '@/services/CardService';
import { CardStatus } from '@/types';
import { SetModal } from '@/components/SetModal';
import { Entypo } from '@expo/vector-icons';

async function getSetInfo(id: string) {
	return await SetService.getOne(id);
}

async function getCardsForSet(id: string) {
	return await CardService.getAll(id);
}

async function loadSetData(id: string, data?: string): Promise<CardSet> {
	if (data) {
		return JSON.parse(data) as CardSet;
	}
	return await getSetInfo(id);
}

async function addCardToSet(id: string, question: string, answer: string): Promise<FlashCard> {
	const result = await CardService.create(id, {
		question: question.trim(),
		answer: answer.trim(),
	} as Partial<FlashCard>);

	return {
		_id: (result as any).insertedId,
		setId: id,
		question: question.trim(),
		answer: answer.trim(),
		status: CardStatus.NEW,
		createdAt: new Date(),
	};
}

export default function CardSetDetail() {
	const { id, data } = useLocalSearchParams<{ id: string, data: string }>();
	const router = useRouter();
	const insets = useSafeAreaInsets();

	const [item, setItem] = useState<CardSet>();
	const [cards, setCards] = useState<FlashCard[]>([]);

	const [learnCardCount, setLearnCardCount] = useState<number>(0);
	const [unknownCardCount, setUnknownCardCount] = useState<number>(0);
	const [totalCardCount, setTotalCardCount] = useState<number>(0);

	const [knownCardPercentage, setKnownCardPercentage] = useState<number>(0);

	const [question, setQuestion] = useState('');
	const [answer, setAnswer] = useState('');

	const [isEditModalVisible, setEditModalVisible] = useState(false);

	if (!id) {
		router.replace('/(app)');
	}

	useEffect(() => {
		loadSetData(id, data).then(setItem);
		getCardsForSet(id).then(setCards);
	}, [id]);

	useEffect(() => {
		if (cards.length === 0) {
			setKnownCardPercentage(0);
			setLearnCardCount(0);
			setUnknownCardCount(0);
			setTotalCardCount(0);
			return;
		}

		const learnCount = cards.filter(c => c.status === CardStatus.LEARN).length;
		const total = cards.length;

		setLearnCardCount(learnCount);
		setTotalCardCount(total);
		setUnknownCardCount(total - learnCount);

		const rawPercentage = (learnCount / total) * 100;

		let finalPercentage;
		if (rawPercentage <= 0) {
			finalPercentage = 0;
		} else if (rawPercentage >= 100) {
			finalPercentage = 100;
		} else {
			finalPercentage = Math.max(35, Math.min(72, Math.round(rawPercentage)));
		}

		setKnownCardPercentage(finalPercentage);
	}, [cards]);

	const handleAddCard = async () => {
		if (!question.trim() || !answer.trim()) {
			return;
		}

		try {
			const newCard = await addCardToSet(id, question, answer);
			setCards([...cards, newCard]);
			setQuestion('');
			setAnswer('');
		} catch (error) {
			console.error('Error adding card:', error);
		}
	};

	const handleUpdateSuccess = (result: any, name: string, description: string) => {
		if (item) {
			setItem({ ...item, name, description });
		}
	};

	const deleteItem = async (cardId: string) => {
		try {
			await CardService.delete(id, cardId);
		} catch (error) {
			console.error('Error deleting card:', error);
		}
	};

	return (
		<View style={{ flex: 1, backgroundColor: Colors.secondary }}>
			<SetModal
				visible={isEditModalVisible}
				onClose={() => setEditModalVisible(false)}
				onSuccess={handleUpdateSuccess}
				setId={id}
				initialName={item?.name}
				initialDescription={item?.description}
			/>
			<ScrollView
				style={{
					flex: 1,
				}}
				contentContainerStyle={{
					paddingTop: insets.top,
					padding: 24,
					paddingBottom: 100,
				}}
			>
				<View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
					<Pressable onPress={() => router.back()}>
						<Ionicons name="arrow-back" size={FONT_SIZES.heading} color={Colors.primary} />
					</Pressable>

					<Pressable onPress={() => setEditModalVisible(true)}>
						<Ionicons name="brush" size={FONT_SIZES.heading2} color={Colors.primary} />
					</Pressable>
				</View>

				<Text style={{
					fontSize: FONT_SIZES.heading,
					color: Colors.primary,
					marginTop: 20,
				}}>
					{item?.name}
				</Text>
				<Text style={{
					fontSize: FONT_SIZES.body,
					color: Colors.primaryLighten,
					marginTop: 8,
				}}>
					{item?.description}
				</Text>
				<View style={{marginTop: 24, marginBottom: 12, width: '100%'}}>
					<View style={{
						height: 36,
						backgroundColor: Colors.secondary,
						borderRadius: 12,
						borderColor: Colors.primary,
						borderWidth: 2,
						overflow: 'hidden',
						justifyContent: 'center'
					}}>
						<View style={{
							height: '100%',
							backgroundColor: Colors.primary,
							width: `${knownCardPercentage}%`,
							marginLeft: -2,
							justifyContent: 'space-between',
							alignItems: 'center'
						}}>
						</View>
						<View style={{
							position: 'absolute',
							justifyContent: 'space-between',
							alignItems: 'center',
							flexDirection: 'row',
							width: '100%',
							paddingHorizontal: 12
						}}>
							<Text style={{
								color: knownCardPercentage === 0 ? Colors.primary : Colors.secondary,
								fontWeight: 'bold',
								fontSize: FONT_SIZES.small
							}}>{learnCardCount} Known</Text>
							<Text style={{
								color: knownCardPercentage === 100 ? Colors.secondary : Colors.primary,
								fontWeight: 'bold',
								fontSize: FONT_SIZES.small
							}}>{unknownCardCount} Left</Text>
						</View>
					</View>
				</View>
				<View style={{
					paddingHorizontal: 12,
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
					}}>New Card</Text>
				</View>

				<View style={{
					borderWidth: 2,
					borderColor: Colors.primary,
					borderRadius: 12,
					padding: 16,
					marginBottom: 12,
					backgroundColor: Colors.secondary,
				}}>
					<TextInput
						style={{
							fontSize: FONT_SIZES.body,
							color: Colors.primary,
							fontWeight: 'bold',
							marginBottom: 8,
						}}
						placeholder="Question"
						value={question}
						onChangeText={setQuestion}
						placeholderTextColor={Colors.primaryLighten}
					/>
					<View style={{
						height: 1,
						backgroundColor: Colors.primary,
						opacity: 0.2,
						marginVertical: 8,
					}} />
					<TextInput
						style={{
							fontSize: FONT_SIZES.body,
							color: Colors.primaryLighten,
							marginBottom: 12,
						}}
						placeholder="Answer"
						value={answer}
						onChangeText={setAnswer}
						placeholderTextColor={Colors.primaryLighten}
					/>
					<Pressable
						style={{
							backgroundColor: Colors.primary,
							borderRadius: 12,
							padding: 12,
						}}
						onPress={handleAddCard}
					>
						<Text
							style={{ color: Colors.secondary, alignSelf: 'center', fontSize: FONT_SIZES.small }}>Add
							Card</Text>
					</Pressable>
				</View>

				<View style={{
					paddingHorizontal: 12,
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
					}}>Set Cards</Text>
				</View>

				{cards.map((item) => (
					<View
						key={item._id}
						style={{
							borderWidth: 2,
							borderColor: Colors.primary,
							borderRadius: 12,
							padding: 16,
							marginBottom: 12,
							backgroundColor: Colors.secondary,
						}}
					>
						<Pressable onPress={() => {
							deleteItem(item._id).then(() =>
							{setCards(cards.filter(c => c._id !== item._id));});
						}} style={{
							position: 'absolute',
							right: -14,
							top: -14,
							borderColor: Colors.primary,
							borderWidth: 2,
							borderRadius: '50%',
							zIndex: 999,
							backgroundColor: Colors.secondary,
							padding: 4,
						}}><Entypo name="cross" size={24} color={Colors.primary} /></Pressable>
						<Text style={{
							fontSize: FONT_SIZES.body,
							color: Colors.primary,
							fontWeight: 'bold',
							marginBottom: 8,
						}}>{item.question}</Text>
						<View style={{
							height: 1,
							backgroundColor: Colors.primary,
							opacity: 0.2,
							marginVertical: 8,
						}} />
						<Text style={{
							fontSize: FONT_SIZES.body,
							color: Colors.primaryLighten,
						}}>{item.answer}</Text>
					</View>
				))}
			</ScrollView>

			<Pressable style={{
				backgroundColor: Colors.primary,
				position: 'absolute',
				bottom: insets.bottom + 24,
				left: 24,
				right: 24,
				borderRadius: 12,
				padding: 12,
				marginHorizontal: 48,
			}} onPress={() => router.push({
				pathname: '/cardset/learn',
				params: { setId: id },
			})}>
				<Text style={{ color: Colors.secondary, alignSelf: 'center', fontSize: FONT_SIZES.body }}>Learn</Text>
			</Pressable>
		</View>
	);
}