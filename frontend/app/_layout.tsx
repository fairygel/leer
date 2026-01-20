import { Stack, useFocusEffect } from 'expo-router';
import { useCallback, useEffect, useState } from 'react';
import { storage } from '@/services/storage';

export default function RootLayout() {
	const [isLoggedIn, setIsLoggedIn] = useState<boolean | null>(null);

	useEffect(() => {
		const checkAuth = async () => {
			const token = await storage.getToken();
			setIsLoggedIn(!!token);
		};

		checkAuth().catch(console.error);
	}, []);

	if (isLoggedIn === null) return null;

	return (
		<Stack
			screenOptions={{
				headerShown: false,
			}}>
			{isLoggedIn ?
				(<Stack.Screen name="(app)" />) :
				(<Stack.Screen name="(auth)" />)}
		</Stack>);
}
