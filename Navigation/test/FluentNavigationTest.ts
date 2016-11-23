/// <reference path="assert.d.ts" />
/// <reference path="mocha.d.ts" />
import * as assert from 'assert';
import { StateNavigator } from '../src/Navigation';

describe('Fluent', function () {
    describe('State', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's', route: 'r' }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s')
                .url;
            assert.strictEqual(url, '/r');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('Second State', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's0', route: 'r0' },
                { key: 's', route: 'r' }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s')
                .url;
            assert.strictEqual(url, '/r');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('State With Trail', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's', route: 'r', trackCrumbTrail: true }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s')
                .url;
            assert.strictEqual(url, '/r');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('Invalid State', function () {
        it('should throw error', function() {
            var stateNavigator = new StateNavigator([
                { key: 's', route: 'r' }
            ]);
            assert.throws(() =>  stateNavigator.fluent().navigate('s0'), /is not a valid State/);
        });
    });

    describe('Transition', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's0', route: 'r0' },
                { key: 's1', route: 'r1' }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s0')
                .navigate('s1')
                .url;
            assert.strictEqual(url, '/r1');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('Transition With Trail', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's0', route: 'r0' },
                { key: 's1', route: 'r1', trackCrumbTrail: true }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s0')
                .navigate('s1')
                .url;
            assert.strictEqual(url, '/r1?crumb=%2Fr0');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('State State', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's', route: 'r' }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s')
                .navigate('s')
                .url;
            assert.strictEqual(url, '/r');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('State State With Trail', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's', route: 'r', trackCrumbTrail: true }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s')
                .navigate('s')
                .url;
            assert.strictEqual(url, '/r');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('Null State', function () {
        it('should throw error', function() {
            var stateNavigator = new StateNavigator([
                { key: 's', route: 'r' }
            ]);
            assert.throws(() =>  stateNavigator.fluent().navigate(null), /is not a valid State/);
        });
    });

    describe('Transition From Without Trail', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's0', route: 'r0', trackCrumbTrail: false },
                { key: 's1', route: 'r1', trackCrumbTrail: true }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s0')
                .navigate('s1')
                .url;
            assert.strictEqual(url, '/r1?crumb=%2Fr0');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });

    describe('Transition With Trail Transition With Trail', function () {
        it('should navigate', function() {
            var stateNavigator = new StateNavigator([
                { key: 's0', route: 'r0' },
                { key: 's1', route: 'r1', trackCrumbTrail: true },
                { key: 's2', route: 'r2', trackCrumbTrail: true }
            ]);
            var url = stateNavigator.fluent()
                .navigate('s0')
                .navigate('s1')
                .navigate('s2')
                .url;
            assert.strictEqual(url, '/r2?crumb=%2Fr0&crumb=%2Fr1');
            assert.strictEqual(stateNavigator.stateContext.url, null);
        });
    });
});
