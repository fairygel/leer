import { Platform } from 'react-native';

export const FONTS = {
	heading: Platform.select({
		ios: 'Georgia',
		android: 'serif',
	}),
}