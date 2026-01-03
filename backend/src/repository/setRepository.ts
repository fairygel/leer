import { ObjectId } from 'mongodb';
import { CardSet } from '../models/CardSet';
import { Service } from 'typedi';
import { Database } from '../config/database';

@Service()
export class SetRepository {
    constructor (
        private database: Database
    ) {}

    private collection() {
        const db = this.database.getDB();
        return db.collection('sets');
    }

    async getAllSets() {
        return await this.collection().find().toArray();
    }

    async getSetById(id: string) {
        return await this.collection().findOne({ _id: new ObjectId(id) });
    }

    async createSet(set: CardSet) {
        return await this.collection().insertOne(set);
    }

    async updateSet(id: string, set: Partial<CardSet>) {
        return await this.collection().updateOne({ _id: new ObjectId(id) }, { $set: set });
    }

    async deleteSet(id: string) {
        return await this.collection().deleteOne({ _id: new ObjectId(id) });
    }
}
