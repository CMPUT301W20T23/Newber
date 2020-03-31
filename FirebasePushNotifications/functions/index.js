'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.firestore
    .document('rideRequests/{requestId}')
    .onUpdate((change, context) => {

    const requestId = context.params.requestId;
    const before = change.before.data();
    const after = change.after.data();

    console.log('Request updated:', requestId);

    if ((before.status === 'PENDING') && (after.status === 'OFFERED')) {
        const riderUid = after.rider;
        const driverUid = after.driver;

        const rider = admin.firestore()
            .collection('users')
            .doc(riderUid)
            .get();

        const driver = admin.firestore()
            .collection('users')
            .doc(driverUid)
            .get();

        return Promise.all([rider, driver])
            .then(result => {
                if ('token' in result[0].data()) {
                    const riderToken = result[0].data().token;
                    const driverUsername = result[1].data().username;

                    const notificationContent = {
                        data: {
                            message: driverUsername + ' offered you a ride!'
                        }
                    };

                    return admin.messaging()
                        .sendToDevice(riderToken, notificationContent)
                        .then(result => {
                            return console.log('Notification sent!');
                    });
                } else {
                    return console.log('No token!');
                }
        });
    } else if ((before.status === 'OFFERED') && (after.status === 'ACCEPTED')) {
        const riderUid = after.rider;
        const driverUid = after.driver;

        const rider = admin.firestore()
            .collection('users')
            .doc(riderUid)
            .get();

        const driver = admin.firestore()
            .collection('users')
            .doc(driverUid)
            .get();

        return Promise.all([rider, driver])
            .then(result => {
                if ('token' in result[1].data()) {
                    const riderUsername = result[0].data().username;
                    const driverToken = result[1].data().token;

                    const notificationContent = {
                        data: {
                            message: riderUsername + ' accepted your ride!'
                        }
                    };

                    return admin.messaging()
                        .sendToDevice(driverToken, notificationContent)
                        .then(result => {
                            return console.log('Notification sent!');
                    });
                } else {
                    return console.log('No token!');
                }
        });
    } else {
        return console.log('No notification needed!');
    }
});
