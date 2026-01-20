import { Modal, Pressable, Text, TextInput, View } from 'react-native';
import { Colors } from '@/constants/Colors';
import { FONT_SIZES } from '@/constants/Sizes';
import { FONTS } from '@/constants/Fonts';
import { useEffect, useState } from 'react';
import { SetService } from '@/services/SetService';

interface SetModalProps {
	visible: boolean;
	onClose: () => void;
	onSuccess?: (result: any, name: string, description: string) => void;
	setId?: string;
	initialName?: string;
	initialDescription?: string;
}

export function SetModal({ visible, onClose, onSuccess, setId, initialName = '', initialDescription = '' }: SetModalProps) {
	const [name, setName] = useState(initialName);
	const [description, setDescription] = useState(initialDescription);

	useEffect(() => {
		if (visible) {
			setName(initialName);
			setDescription(initialDescription);
		}
	}, [visible, initialName, initialDescription]);

	const handleSubmit = async () => {
		if (!name) return;
		try {
			let result;
			if (setId) {
				result = await SetService.update(setId, { name, description });
			} else {
				result = await SetService.create({ name, description });
			}
			if (onSuccess) onSuccess(result, name, description);
			onClose();
			setName('');
			setDescription('');
		} catch (e) {
			console.error(e);
		}
	};

	return (
		<Modal
			animationType="fade"
			transparent={true}
			visible={visible}
			statusBarTranslucent={true}
			onRequestClose={onClose}
		>
			<Pressable onPress={onClose}
					   style={{
						   flex: 1,
						   justifyContent: 'center',
						   alignItems: 'center',
						   backgroundColor: 'rgba(0,0,0,0.4)',
					   }}>
				<Pressable onPress={(e) => e.stopPropagation()} style={{
					width: 350,
					backgroundColor: Colors.secondary,
					borderRadius: 20,
					padding: 35,
					alignItems: 'center',
				}}>
					<View>
						<Text style={{
							fontSize: FONT_SIZES.heading,
							color: Colors.primary,
							fontFamily: FONTS.heading,
							marginBottom: 24
						}}>{setId ? 'Update Card Set' : 'New Card Set'}</Text>
						<Text style={{ marginLeft: 4, color: Colors.primary }}>Name</Text>
						<TextInput style={{
							borderStyle: 'solid',
							borderWidth: 2,
							borderRadius: 12,
							borderColor: Colors.primary,
							padding: 12,
							color: Colors.primaryLighten,
							fontSize: FONT_SIZES.small,
							marginTop: 4,
							marginBottom: 12,
							width: 280
						}} placeholder="Set Name" value={name} onChangeText={setName}></TextInput>

						<Text style={{ marginLeft: 4, color: Colors.primary }}>Description</Text>
						<TextInput style={{
							borderStyle: 'solid',
							borderWidth: 2,
							borderRadius: 12,
							borderColor: Colors.primary,
							padding: 12,
							marginTop: 4,
							marginBottom: 12,
							color: Colors.primaryLighten,
							fontSize: FONT_SIZES.small,
							paddingRight: 40,
							width: 280,
							height: 100,
							textAlignVertical: 'top'
						}} placeholder="Description" value={description}
								   multiline={true}
								   onChangeText={setDescription}>
						</TextInput>

						<Pressable style={{
							width: 86,
							height: 48,
							alignItems: 'center',
							justifyContent: 'center',
							borderColor: Colors.primary,
							borderWidth: 2,
							borderRadius: 12,
							backgroundColor: Colors.secondary,
							alignSelf: 'flex-end',
							position: 'relative',
							top: 12,
							right: -12,
							marginTop: 12
						}} onPress={handleSubmit}>
							<Text style={{
								color: Colors.primary,
								fontSize: FONT_SIZES.small
							}}>{setId ? 'Update!' : 'Create!'}</Text>
						</Pressable>
					</View>
				</Pressable>
			</Pressable>
		</Modal>
	);
}

