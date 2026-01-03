import 'reflect-metadata';
import Container from 'typedi';
import { Leer } from './Leer';

async function bootstrap() {
	try {
		const app = Container.get(Leer);

		await app.start(5000);
	} catch (error: any) {
		console.error(
			`\x1b[91m[leer] Bootstrap failed: ${error.message}\x1b[0m`
		);
		process.exit(1);
	}
}

bootstrap().then(() => {}).catch(err => {
	console.log(err);});
