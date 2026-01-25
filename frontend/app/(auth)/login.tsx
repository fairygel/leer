import { Pressable, Text, TextInput, View } from 'react-native';
import { FONT_SIZES } from '@/constants/Sizes';
import { Colors } from '@/constants/Colors';
import { Link, router } from 'expo-router';
import Ionicons from '@expo/vector-icons/Ionicons';
import { useState } from 'react';
import { storage } from '@/services/storage';
import { fetcher } from '@/services/fetcher';

async function makeRequest(name: string, password: string) {
	try {
		const response = await fetcher('/auth/login', 'post', {
			body: JSON.stringify({
				username: name,
				password: password,
			}),
		});

		if (response.status !== 200) {
			console.log("Can't register");
			console.log(await response.json());
			return
		}
		const jsonResponse = await response.json();
		await storage.saveToken(jsonResponse.token);

	} catch (e) {
		console.log(e);
	}
}

export default function Login() {
	const [showPassword, setShowPassword] = useState(false);
	const [name, setName] = useState('');
	const [password, setPassword] = useState('');
	const handleLogin = async () => {
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
					height: 500,
					borderRadius: 24,
					backgroundColor: Colors.secondary,
					justifyContent: 'space-around',
					paddingVertical: 20,
					paddingHorizontal: 56,
				}}>

				<View style={{paddingTop: 24, paddingLeft: 4}}>
					<Text style={{
						color: Colors.primary,
						fontSize: FONT_SIZES.title,
						fontFamily: 'Georgia',
					}}>Welcume!</Text>
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
					}} placeholder="Your name.." value={name} onChangeText={setName}></TextInput>

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
						}} placeholder="Your password.." secureTextEntry={!showPassword} value={password} onChangeText={setPassword}>
						</TextInput>
						<Pressable
							onPress={() => setShowPassword(!showPassword)}
							style={{ position: 'absolute', right: 18, top: 18 }}
						><Ionicons
								name={showPassword ? 'eye' : 'eye-off'}
								size={20}
								color="#516a69"
							/>
						</Pressable>
					</View>
				</View>

				<Pressable 
					onPress={handleLogin}
					style={{
						backgroundColor: Colors.primary,
						width: '100%',
						height: 60,
						borderRadius: 12,
						alignItems: 'center',
						justifyContent: 'center',
					}}>
					<Text style={{ fontSize: FONT_SIZES.body, color: Colors.secondary }}>
						Login{' '}
						<Ionicons name="enter-outline" size={FONT_SIZES.body} color={Colors.secondary} />
					</Text>
				</Pressable>

				<Text style={{ fontSize: FONT_SIZES.smaller, color: Colors.primaryLighten, textAlign: 'center' }}>
					Don't Have account? Try{' '}
					<Link style={{ color: Colors.primaryDarken, textDecorationLine: 'underline' }} href="/register">
						Sign Up
					</Link>
				</Text>

			</View>
		</View>
	);
}
