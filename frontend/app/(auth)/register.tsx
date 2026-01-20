import { Alert, Pressable, Text, TextInput, View } from 'react-native';
import { FONT_SIZES } from '@/constants/Sizes';
import { Colors } from '@/constants/Colors';
import { Link, router } from 'expo-router';
import Ionicons from '@expo/vector-icons/Ionicons';
import { useState } from 'react';
import { storage } from '@/services/storage';
import { AuthService } from '@/services/AuthService';
import { FONTS } from '@/constants/Fonts';

async function makeRequest(name: string, password: string) {
	const jsonResponse = await AuthService.register({
		username: name,
		password: password,
	});

	await storage.saveToken(jsonResponse.userId);

}

export default function Register() {
	const [showPassword, setShowPassword] = useState(false);
	const [name, setName] = useState('');
	const [password, setPassword] = useState('');
	const [repeatPassword, setRepeatPassword] = useState('');
	const handleRegister = async () => {
		if (!name || !password || !repeatPassword) {
			Alert.alert('Error', 'All fields should be filled.');
			return;
		}

		if (password !== repeatPassword) {
			Alert.alert('Error', 'Passwords don\'t match.');
			return;
		}
		try {
			await makeRequest(name, password);
			router.replace('/(app)');
		} catch (e) {
			console.log(e);
		}
	};

	return (
		<View
			style={{
				flex: 1,
				justifyContent: 'center',
				alignItems: 'center',
				backgroundColor: Colors.primary,
			}}
		>

			<View
				style={{
					width: '60%',
					minWidth: 380,
					maxWidth: 600,
					height: 600,
					borderRadius: 24,
					backgroundColor: Colors.secondary,
					justifyContent: 'space-around',
					paddingVertical: 20,
					paddingHorizontal: 56,
				}}>

				<View style={{ paddingTop: 24, paddingLeft: 4 }}>
					<Text style={{
						color: Colors.primary,
						fontSize: FONT_SIZES.title,
						fontFamily: FONTS.heading,
					}}>Welcome!</Text>
				</View>

				<View style={{ justifyContent: 'flex-start', paddingTop: 24 }}>
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
					}}
							   placeholder="Your name.."
							   value={name}
							   onChangeText={setName}>
					</TextInput>

					<Text style={{ marginLeft: 4, color: Colors.primary }}>Password</Text>
					<View>
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
						}}
								   placeholder="Your password.."
								   secureTextEntry={!showPassword}
								   value={password}
								   onChangeText={setPassword}>
						</TextInput>
						<Pressable
							onPress={() => setShowPassword(!showPassword)}
							style={{ position: 'absolute', right: 16, top: 16 }}
						><Ionicons
							name={showPassword ? 'eye' : 'eye-off'}
							size={20}
							color="#516a69"
						/>
						</Pressable>
					</View>

					<Text style={{ marginLeft: 4, color: Colors.primary }}>Repeat Password</Text>
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
					}}
							   placeholder="Repeat your password.."
							   secureTextEntry={!showPassword}
							   value={repeatPassword}
							   onChangeText={setRepeatPassword}>
					</TextInput>
				</View>

				<Pressable
					onPress={handleRegister}
					style={{
						backgroundColor: Colors.primary,
						width: '100%',
						height: 60,
						borderRadius: 12,
						alignItems: 'center',
						justifyContent: 'center',
					}}>
					<Text style={{ fontSize: FONT_SIZES.body, color: Colors.secondary }}>
						Sign Up{' '}
						<Ionicons name="enter-outline" size={FONT_SIZES.body} color={Colors.secondary} />
					</Text>
				</Pressable>

				<Text style={{ fontSize: FONT_SIZES.smaller, color: Colors.primaryLighten, textAlign: 'center' }}>
					Already have account? Try{' '}
					<Link style={{ color: Colors.primaryDarken, textDecorationLine: 'underline' }} href="/login">
						Login
					</Link>
				</Text>

			</View>
		</View>
	);
}
